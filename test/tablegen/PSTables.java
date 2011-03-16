package tablegen;

public class PSTables {

	private static final float SQRT2 = 1.41421356237309504880f; //sqrt(2)
	private static final float SQRT1_2 = 0.70710678118654752440f; //1/sqrt(2)
	private static final int ALLPASS_BANDS20 = 30;
	private static final int ALLPASS_BANDS34 = 50;
	private static final int AP_LINKS = 3;
	private static final float[] G0_Q8 = {
		0.00746082949812f, 0.02270420949825f, 0.04546865930473f, 0.07266113929591f,
		0.09885108575264f, 0.11793710567217f, 0.125f
	};
	private static final float[] G0_Q12 = {
		0.04081179924692f, 0.03812810994926f, 0.05144908135699f, 0.06399831151592f,
		0.07428313801106f, 0.08100347892914f, 0.08333333333333f
	};
	private static final float[] G1_Q8 = {
		0.01565675600122f, 0.03752716391991f, 0.05417891378782f, 0.08417044116767f,
		0.10307344158036f, 0.12222452249753f, 0.125f
	};
	private static final float[] G2_Q4 = {
		-0.05908211155639f, -0.04871498374946f, 0.0f, 0.07778723915851f,
		0.16486303567403f, 0.23279856662996f, 0.25f
	};
	private static final float[] IPDOPD_SIN = {0, SQRT1_2, 1, SQRT1_2, 0, -SQRT1_2, -1, -SQRT1_2};
	private static final float[] IPDOPD_COS = {1, SQRT1_2, 0, -SQRT1_2, -1, -SQRT1_2, 0, SQRT1_2};
	private static final float[] IID_PAR_DEQUANT = {
		//iid_par_dequant_default
		0.05623413251903f, 0.12589254117942f, 0.19952623149689f, 0.31622776601684f,
		0.44668359215096f, 0.63095734448019f, 0.79432823472428f, 1f,
		1.25892541179417f, 1.58489319246111f, 2.23872113856834f, 3.16227766016838f,
		5.01187233627272f, 7.94328234724282f, 17.7827941003892f,
		//iid_par_dequant_fine
		0.00316227766017f, 0.00562341325190f, 0.01f, 0.01778279410039f,
		0.03162277660168f, 0.05623413251903f, 0.07943282347243f, 0.11220184543020f,
		0.15848931924611f, 0.22387211385683f, 0.31622776601684f, 0.39810717055350f,
		0.50118723362727f, 0.63095734448019f, 0.79432823472428f, 1f,
		1.25892541179417f, 1.58489319246111f, 1.99526231496888f, 2.51188643150958f,
		3.16227766016838f, 4.46683592150963f, 6.30957344480193f, 8.91250938133745f,
		12.5892541179417f, 17.7827941003892f, 31.6227766016838f, 56.2341325190349f,
		100f, 177.827941003892f, 316.227766016837f
	};
	private static final float[] ICC_INVQ = {
		1.0f, 0.937f, 0.84118f, 0.60092f, 0.36764f, 0.0f, -0.589f, -1.0f
	};
	private static final float[] ACOS_ICC_INVQ = {
		0.0f, 0.35685527f, 0.57133466f, 0.92614472f, 1.1943263f, (float) Math.PI/2.0f, 2.2006171f, (float) Math.PI
	};
	private static final int[] F_CENTER_20 = {
		-3, -1, 1, 3, 5, 7, 10, 14, 18, 22
	};
	private static final int[] F_CENTER_34 = {
		2, 6, 10, 14, 18, 22, 26, 30,
		34, -10, -6, -2, 51, 57, 15, 21,
		27, 33, 39, 45, 54, 66, 78, 42,
		102, 66, 78, 90, 102, 114, 126, 90
	};
	private static final float[] FRACTIONAL_DELAY_LINKS = {0.43f, 0.75f, 0.347f};
	private static final float FRACTIONAL_DELAY_GAIN = 0.39f;

	public static void main(String[] args) {
		//pd smooth
		final float[] pdRe = new float[8*8*8];
		final float[] pdIm = new float[8*8*8];
		calculatePDSmooth(pdRe, pdIm);
		Utils.printTable(pdRe, "PD_RE_SMOOTH");
		Utils.printTable(pdIm, "PD_IM_SMOOTH");

		//HA, HB
		final float[][][] ha = new float[46][8][4];
		final float[][][] hb = new float[46][8][4];
		calculateH(ha, hb);
		Utils.printTable(ha, "HA");
		Utils.printTable(hb, "HB");

		//fracts
		final float[][][][] qFractAllpass = new float[2][50][3][2];
		final float[][][] phiFract = new float[2][50][2];
		calculateFracts(qFractAllpass, phiFract);
		Utils.printTable(qFractAllpass, "Q_FRACT_ALLPASS");
		Utils.printTable(phiFract, "PHI_FRACT");

		//filters
		float[][][] F20_0_8 = makeFiltersFromProto(G0_Q8, 8);
		Utils.printTable(F20_0_8, "F20_0_8");

		float[][][] F34_0_12 = makeFiltersFromProto(G0_Q12, 12);
		Utils.printTable(F34_0_12, "F34_0_12");

		float[][][] F34_1_8 = makeFiltersFromProto(G1_Q8, 8);
		Utils.printTable(F34_1_8, "F34_1_8");

		float[][][] F34_2_4 = makeFiltersFromProto(G2_Q4, 4);
		Utils.printTable(F34_2_4, "F34_2_4");
	}

	private static void calculatePDSmooth(float[] pdRe, float[] pdIm) {
		int pd1, pd2;
		for(int pd0 = 0; pd0<8; pd0++) {
			float pd0_re = IPDOPD_COS[pd0];
			float pd0_im = IPDOPD_SIN[pd0];
			for(pd1 = 0; pd1<8; pd1++) {
				float pd1_re = IPDOPD_COS[pd1];
				float pd1_im = IPDOPD_SIN[pd1];
				for(pd2 = 0; pd2<8; pd2++) {
					float pd2_re = IPDOPD_COS[pd2];
					float pd2_im = IPDOPD_SIN[pd2];
					float re_smooth = 0.25f*pd0_re+0.5f*pd1_re+pd2_re;
					float im_smooth = 0.25f*pd0_im+0.5f*pd1_im+pd2_im;
					float pd_mag = 1/(float) Math.sqrt(im_smooth*im_smooth+re_smooth*re_smooth);
					pdRe[pd0*64+pd1*8+pd2] = re_smooth*pd_mag;
					pdIm[pd0*64+pd1*8+pd2] = im_smooth*pd_mag;
				}
			}
		}
	}

	private static void calculateH(float[][][] ha, float[][][] hb) {
		int icc;
		float c, c1, c2;
		float alpha, beta, gamma, mu, rho;
		float alpha_c, alpha_s, gamma_c, gamma_s;
		for(int iid = 0; iid<46; iid++) {
			c = IID_PAR_DEQUANT[iid];
			c1 = SQRT2/(float) Math.sqrt(1.0f+c*c);
			c2 = c*c1;
			for(icc = 0; icc<8; icc++) {
				alpha = 0.5f*ACOS_ICC_INVQ[icc];
				beta = alpha*(c1-c2)*SQRT1_2;
				ha[iid][icc][0] = c2*(float) Math.cos(beta+alpha);
				ha[iid][icc][1] = c1*(float) Math.cos(beta-alpha);
				ha[iid][icc][2] = c2*(float) Math.sin(beta+alpha);
				ha[iid][icc][3] = c1*(float) Math.sin(beta-alpha);

				rho = Math.max(ICC_INVQ[icc], 0.05f);
				alpha = 0.5f*(float) Math.atan2(2.0f*c*rho, c*c-1.0f);
				mu = c+1.0f/c;
				mu = (float) Math.sqrt(1+(4*rho*rho-4)/(mu*mu));
				gamma = (float) Math.atan(Math.sqrt((1.0f-mu)/(1.0f+mu)));
				if(alpha<0) alpha += Math.PI/2;
				alpha_c = (float) Math.cos(alpha);
				alpha_s = (float) Math.sin(alpha);
				gamma_c = (float) Math.cos(gamma);
				gamma_s = (float) Math.sin(gamma);
				hb[iid][icc][0] = SQRT2*alpha_c*gamma_c;
				hb[iid][icc][1] = SQRT2*alpha_s*gamma_c;
				hb[iid][icc][2] = -SQRT2*alpha_s*gamma_s;
				hb[iid][icc][3] = SQRT2*alpha_c*gamma_s;
			}
		}
	}

	private static void calculateFracts(float[][][][] qFractAllpass, float[][][] phiFract) {
		int k, m;
		double f_center, theta;
		for(k = 0; k<ALLPASS_BANDS20; k++) {
			if(k<F_CENTER_20.length) f_center = F_CENTER_20[k]*0.125;
			else f_center = k-6.5f;
			for(m = 0; m<AP_LINKS; m++) {
				theta = -Math.PI*FRACTIONAL_DELAY_LINKS[m]*f_center;
				qFractAllpass[0][k][m][0] = (float) Math.cos(theta);
				qFractAllpass[0][k][m][1] = (float) Math.sin(theta);
			}
			theta = (float) -Math.PI*FRACTIONAL_DELAY_GAIN*f_center;
			phiFract[0][k][0] = (float) Math.cos(theta);
			phiFract[0][k][1] = (float) Math.sin(theta);
		}
		for(k = 0; k<ALLPASS_BANDS34; k++) {
			if(k<F_CENTER_34.length) f_center = F_CENTER_34[k]/24.;
			else f_center = k-26.5f;
			for(m = 0; m<AP_LINKS; m++) {
				theta = -Math.PI*FRACTIONAL_DELAY_LINKS[m]*f_center;
				qFractAllpass[1][k][m][0] = (float) Math.cos(theta);
				qFractAllpass[1][k][m][1] = (float) Math.sin(theta);
			}
			theta = -Math.PI*FRACTIONAL_DELAY_GAIN*f_center;
			phiFract[1][k][0] = (float) Math.cos(theta);
			phiFract[1][k][1] = (float) Math.sin(theta);
		}
	}

	private static float[][][] makeFiltersFromProto(float[] proto, int bands) {
		final float[][][] filter = new float[bands][7][2];
		final double PI2 = Math.PI*2;

		int q, n;
		double theta;
		for(q = 0; q<bands; q++) {
			for(n = 0; n<7; n++) {
				theta = PI2*(q+0.5)*(n-6)/bands;
				filter[q][n][0] = proto[n]*(float) Math.cos(theta);
				filter[q][n][1] = proto[n]*(float) -Math.sin(theta);
			}
		}
		return filter;
	}
}