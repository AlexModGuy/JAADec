package net.sourceforge.jaad.aac.sbr;

class NoiseEnvelope {

	private static final float[] E_deq_tab = {
		64.0f, 128.0f, 256.0f, 512.0f, 1024.0f, 2048.0f, 4096.0f, 8192.0f,
		16384.0f, 32768.0f, 65536.0f, 131072.0f, 262144.0f, 524288.0f, 1.04858E+006f, 2.09715E+006f,
		4.1943E+006f, 8.38861E+006f, 1.67772E+007f, 3.35544E+007f, 6.71089E+007f, 1.34218E+008f, 2.68435E+008f, 5.36871E+008f,
		1.07374E+009f, 2.14748E+009f, 4.29497E+009f, 8.58993E+009f, 1.71799E+010f, 3.43597E+010f, 6.87195E+010f, 1.37439E+011f,
		2.74878E+011f, 5.49756E+011f, 1.09951E+012f, 2.19902E+012f, 4.39805E+012f, 8.79609E+012f, 1.75922E+013f, 3.51844E+013f,
		7.03687E+013f, 1.40737E+014f, 2.81475E+014f, 5.6295E+014f, 1.1259E+015f, 2.2518E+015f, 4.5036E+015f, 9.0072E+015f,
		1.80144E+016f, 3.60288E+016f, 7.20576E+016f, 1.44115E+017f, 2.8823E+017f, 5.76461E+017f, 1.15292E+018f, 2.30584E+018f,
		4.61169E+018f, 9.22337E+018f, 1.84467E+019f, 3.68935E+019f, 7.3787E+019f, 1.47574E+020f, 2.95148E+020f, 5.90296E+020f
	};

	/* table for Q_div2 values when no coupling */
	private static final float[] Q_div2_tab = {
		0.984615f, 0.969697f,
		0.941176f, 0.888889f,
		0.8f, 0.666667f,
		0.5f, 0.333333f,
		0.2f, 0.111111f,
		0.0588235f, 0.030303f,
		0.0153846f, 0.00775194f,
		0.00389105f, 0.00194932f,
		0.00097561f, 0.000488043f,
		0.000244081f, 0.000122055f,
		6.10314E-005f, 3.05166E-005f,
		1.52586E-005f, 7.62934E-006f,
		3.81468E-006f, 1.90734E-006f,
		9.53673E-007f, 4.76837E-007f,
		2.38419E-007f, 1.19209E-007f,
		5.96046E-008f};

	private static final float[][] Q_div2_tab_left = {
		{0.0302959f, 0.111015f, 0.332468f, 0.663212f, 0.882759f, 0.962406f, 0.984615f, 0.990329f, 0.991768f, 0.992128f, 0.992218f, 0.992241f, 0.992246f},
		{0.0153809f, 0.0587695f, 0.199377f, 0.496124f, 0.790123f, 0.927536f, 0.969697f, 0.980843f, 0.98367f, 0.984379f, 0.984556f, 0.984601f, 0.984612f},
		{0.00775006f, 0.0302744f, 0.110727f, 0.329897f, 0.653061f, 0.864865f, 0.941176f, 0.962406f, 0.967864f, 0.969238f, 0.969582f, 0.969668f, 0.96969f},
		{0.0038901f, 0.0153698f, 0.0586081f, 0.197531f, 0.484848f, 0.761905f, 0.888889f, 0.927536f, 0.937729f, 0.940312f, 0.94096f, 0.941122f, 0.941163f},
		{0.00194884f, 0.00774443f, 0.0301887f, 0.109589f, 0.32f, 0.615385f, 0.8f, 0.864865f, 0.882759f, 0.887348f, 0.888503f, 0.888792f, 0.888865f},
		{0.000975372f, 0.00388727f, 0.0153257f, 0.057971f, 0.190476f, 0.444444f, 0.666667f, 0.761905f, 0.790123f, 0.797508f, 0.799375f, 0.799844f, 0.799961f},
		{0.000487924f, 0.00194742f, 0.00772201f, 0.0298507f, 0.105263f, 0.285714f, 0.5f, 0.615385f, 0.653061f, 0.663212f, 0.6658f, 0.66645f, 0.666612f},
		{0.000244021f, 0.000974659f, 0.00387597f, 0.0151515f, 0.0555556f, 0.166667f, 0.333333f, 0.444444f, 0.484848f, 0.496124f, 0.499025f, 0.499756f, 0.499939f},
		{0.000122026f, 0.000487567f, 0.00194175f, 0.00763359f, 0.0285714f, 0.0909091f, 0.2f, 0.285714f, 0.32f, 0.329897f, 0.332468f, 0.333116f, 0.333279f},
		{6.10165E-005f, 0.000243843f, 0.000971817f, 0.00383142f, 0.0144928f, 0.047619f, 0.111111f, 0.166667f, 0.190476f, 0.197531f, 0.199377f, 0.199844f, 0.199961f},
		{3.05092E-005f, 0.000121936f, 0.000486145f, 0.00191939f, 0.00729927f, 0.0243902f, 0.0588235f, 0.0909091f, 0.105263f, 0.109589f, 0.110727f, 0.111015f, 0.111087f},
		{1.52548E-005f, 6.09719E-005f, 0.000243132f, 0.000960615f, 0.003663f, 0.0123457f, 0.030303f, 0.047619f, 0.0555556f, 0.057971f, 0.0586081f, 0.0587695f, 0.05881f},
		{7.62747E-006f, 3.04869E-005f, 0.000121581f, 0.000480538f, 0.00183486f, 0.00621118f, 0.0153846f, 0.0243902f, 0.0285714f, 0.0298507f, 0.0301887f, 0.0302744f, 0.0302959f},
		{3.81375E-006f, 1.52437E-005f, 6.0794E-005f, 0.000240327f, 0.000918274f, 0.00311526f, 0.00775194f, 0.0123457f, 0.0144928f, 0.0151515f, 0.0153257f, 0.0153698f, 0.0153809f},
		{1.90688E-006f, 7.62189E-006f, 3.03979E-005f, 0.000120178f, 0.000459348f, 0.00156006f, 0.00389105f, 0.00621118f, 0.00729927f, 0.00763359f, 0.00772201f, 0.00774443f, 0.00775006f},
		{9.53441E-007f, 3.81096E-006f, 1.51992E-005f, 6.00925E-005f, 0.000229727f, 0.00078064f, 0.00194932f, 0.00311526f, 0.003663f, 0.00383142f, 0.00387597f, 0.00388727f, 0.0038901f},
		{4.76721E-007f, 1.90548E-006f, 7.59965E-006f, 3.00472E-005f, 0.000114877f, 0.000390472f, 0.00097561f, 0.00156006f, 0.00183486f, 0.00191939f, 0.00194175f, 0.00194742f, 0.00194884f},
		{2.3836E-007f, 9.52743E-007f, 3.79984E-006f, 1.50238E-005f, 5.74416E-005f, 0.000195274f, 0.000488043f, 0.00078064f, 0.000918274f, 0.000960615f, 0.000971817f, 0.000974659f, 0.000975372f},
		{1.1918E-007f, 4.76372E-007f, 1.89992E-006f, 7.51196E-006f, 2.87216E-005f, 9.76467E-005f, 0.000244081f, 0.000390472f, 0.000459348f, 0.000480538f, 0.000486145f, 0.000487567f, 0.000487924f},
		{5.95901E-008f, 2.38186E-007f, 9.49963E-007f, 3.756E-006f, 1.4361E-005f, 4.88257E-005f, 0.000122055f, 0.000195274f, 0.000229727f, 0.000240327f, 0.000243132f, 0.000243843f, 0.000244021f},
		{2.9795E-008f, 1.19093E-007f, 4.74982E-007f, 1.878E-006f, 7.18056E-006f, 2.44135E-005f, 6.10314E-005f, 9.76467E-005f, 0.000114877f, 0.000120178f, 0.000121581f, 0.000121936f, 0.000122026f},
		{1.48975E-008f, 5.95465E-008f, 2.37491E-007f, 9.39002E-007f, 3.59029E-006f, 1.22069E-005f, 3.05166E-005f, 4.88257E-005f, 5.74416E-005f, 6.00925E-005f, 6.0794E-005f, 6.09719E-005f, 6.10165E-005f},
		{7.44876E-009f, 2.97732E-008f, 1.18745E-007f, 4.69501E-007f, 1.79515E-006f, 6.10348E-006f, 1.52586E-005f, 2.44135E-005f, 2.87216E-005f, 3.00472E-005f, 3.03979E-005f, 3.04869E-005f, 3.05092E-005f},
		{3.72438E-009f, 1.48866E-008f, 5.93727E-008f, 2.34751E-007f, 8.97575E-007f, 3.05175E-006f, 7.62934E-006f, 1.22069E-005f, 1.4361E-005f, 1.50238E-005f, 1.51992E-005f, 1.52437E-005f, 1.52548E-005f},
		{1.86219E-009f, 7.44331E-009f, 2.96864E-008f, 1.17375E-007f, 4.48788E-007f, 1.52588E-006f, 3.81468E-006f, 6.10348E-006f, 7.18056E-006f, 7.51196E-006f, 7.59965E-006f, 7.62189E-006f, 7.62747E-006f},
		{9.31095E-010f, 3.72166E-009f, 1.48432E-008f, 5.86876E-008f, 2.24394E-007f, 7.62939E-007f, 1.90734E-006f, 3.05175E-006f, 3.59029E-006f, 3.756E-006f, 3.79984E-006f, 3.81096E-006f, 3.81375E-006f},
		{4.65548E-010f, 1.86083E-009f, 7.42159E-009f, 2.93438E-008f, 1.12197E-007f, 3.8147E-007f, 9.53673E-007f, 1.52588E-006f, 1.79515E-006f, 1.878E-006f, 1.89992E-006f, 1.90548E-006f, 1.90688E-006f},
		{2.32774E-010f, 9.30414E-010f, 3.71079E-009f, 1.46719E-008f, 5.60985E-008f, 1.90735E-007f, 4.76837E-007f, 7.62939E-007f, 8.97575E-007f, 9.39002E-007f, 9.49963E-007f, 9.52743E-007f, 9.53441E-007f},
		{1.16387E-010f, 4.65207E-010f, 1.8554E-009f, 7.33596E-009f, 2.80492E-008f, 9.53674E-008f, 2.38419E-007f, 3.8147E-007f, 4.48788E-007f, 4.69501E-007f, 4.74982E-007f, 4.76372E-007f, 4.76721E-007f},
		{5.81935E-011f, 2.32603E-010f, 9.27699E-010f, 3.66798E-009f, 1.40246E-008f, 4.76837E-008f, 1.19209E-007f, 1.90735E-007f, 2.24394E-007f, 2.34751E-007f, 2.37491E-007f, 2.38186E-007f, 2.3836E-007f},
		{2.90967E-011f, 1.16302E-010f, 4.63849E-010f, 1.83399E-009f, 7.01231E-009f, 2.38419E-008f, 5.96046E-008f, 9.53674E-008f, 1.12197E-007f, 1.17375E-007f, 1.18745E-007f, 1.19093E-007f, 1.1918E-007f}
	};

	private static final float[][] Q_div2_tab_right = {
		{0.992246f, 0.992241f, 0.992218f, 0.992128f, 0.991768f, 0.990329f, 0.984615f, 0.962406f, 0.882759f, 0.663212f, 0.332468f, 0.111015f, 0.0302959f},
		{0.984612f, 0.984601f, 0.984556f, 0.984379f, 0.98367f, 0.980843f, 0.969697f, 0.927536f, 0.790123f, 0.496124f, 0.199377f, 0.0587695f, 0.0153809f},
		{0.96969f, 0.969668f, 0.969582f, 0.969238f, 0.967864f, 0.962406f, 0.941176f, 0.864865f, 0.653061f, 0.329897f, 0.110727f, 0.0302744f, 0.00775006f},
		{0.941163f, 0.941122f, 0.94096f, 0.940312f, 0.937729f, 0.927536f, 0.888889f, 0.761905f, 0.484848f, 0.197531f, 0.0586081f, 0.0153698f, 0.0038901f},
		{0.888865f, 0.888792f, 0.888503f, 0.887348f, 0.882759f, 0.864865f, 0.8f, 0.615385f, 0.32f, 0.109589f, 0.0301887f, 0.00774443f, 0.00194884f},
		{0.799961f, 0.799844f, 0.799375f, 0.797508f, 0.790123f, 0.761905f, 0.666667f, 0.444444f, 0.190476f, 0.057971f, 0.0153257f, 0.00388727f, 0.000975372f},
		{0.666612f, 0.66645f, 0.6658f, 0.663212f, 0.653061f, 0.615385f, 0.5f, 0.285714f, 0.105263f, 0.0298507f, 0.00772201f, 0.00194742f, 0.000487924f},
		{0.499939f, 0.499756f, 0.499025f, 0.496124f, 0.484848f, 0.444444f, 0.333333f, 0.166667f, 0.0555556f, 0.0151515f, 0.00387597f, 0.000974659f, 0.000244021f},
		{0.333279f, 0.333116f, 0.332468f, 0.329897f, 0.32f, 0.285714f, 0.2f, 0.0909091f, 0.0285714f, 0.00763359f, 0.00194175f, 0.000487567f, 0.000122026f},
		{0.199961f, 0.199844f, 0.199377f, 0.197531f, 0.190476f, 0.166667f, 0.111111f, 0.047619f, 0.0144928f, 0.00383142f, 0.000971817f, 0.000243843f, 6.10165E-005f},
		{0.111087f, 0.111015f, 0.110727f, 0.109589f, 0.105263f, 0.0909091f, 0.0588235f, 0.0243902f, 0.00729927f, 0.00191939f, 0.000486145f, 0.000121936f, 3.05092E-005f},
		{0.05881f, 0.0587695f, 0.0586081f, 0.057971f, 0.0555556f, 0.047619f, 0.030303f, 0.0123457f, 0.003663f, 0.000960615f, 0.000243132f, 6.09719E-005f, 1.52548E-005f},
		{0.0302959f, 0.0302744f, 0.0301887f, 0.0298507f, 0.0285714f, 0.0243902f, 0.0153846f, 0.00621118f, 0.00183486f, 0.000480538f, 0.000121581f, 3.04869E-005f, 7.62747E-006f},
		{0.0153809f, 0.0153698f, 0.0153257f, 0.0151515f, 0.0144928f, 0.0123457f, 0.00775194f, 0.00311526f, 0.000918274f, 0.000240327f, 6.0794E-005f, 1.52437E-005f, 3.81375E-006f},
		{0.00775006f, 0.00774443f, 0.00772201f, 0.00763359f, 0.00729927f, 0.00621118f, 0.00389105f, 0.00156006f, 0.000459348f, 0.000120178f, 3.03979E-005f, 7.62189E-006f, 1.90688E-006f},
		{0.0038901f, 0.00388727f, 0.00387597f, 0.00383142f, 0.003663f, 0.00311526f, 0.00194932f, 0.00078064f, 0.000229727f, 6.00925E-005f, 1.51992E-005f, 3.81096E-006f, 9.53441E-007f},
		{0.00194884f, 0.00194742f, 0.00194175f, 0.00191939f, 0.00183486f, 0.00156006f, 0.00097561f, 0.000390472f, 0.000114877f, 3.00472E-005f, 7.59965E-006f, 1.90548E-006f, 4.76721E-007f},
		{0.000975372f, 0.000974659f, 0.000971817f, 0.000960615f, 0.000918274f, 0.00078064f, 0.000488043f, 0.000195274f, 5.74416E-005f, 1.50238E-005f, 3.79984E-006f, 9.52743E-007f, 2.3836E-007f},
		{0.000487924f, 0.000487567f, 0.000486145f, 0.000480538f, 0.000459348f, 0.000390472f, 0.000244081f, 9.76467E-005f, 2.87216E-005f, 7.51196E-006f, 1.89992E-006f, 4.76372E-007f, 1.1918E-007f},
		{0.000244021f, 0.000243843f, 0.000243132f, 0.000240327f, 0.000229727f, 0.000195274f, 0.000122055f, 4.88257E-005f, 1.4361E-005f, 3.756E-006f, 9.49963E-007f, 2.38186E-007f, 5.95901E-008f},
		{0.000122026f, 0.000121936f, 0.000121581f, 0.000120178f, 0.000114877f, 9.76467E-005f, 6.10314E-005f, 2.44135E-005f, 7.18056E-006f, 1.878E-006f, 4.74982E-007f, 1.19093E-007f, 2.9795E-008f},
		{6.10165E-005f, 6.09719E-005f, 6.0794E-005f, 6.00925E-005f, 5.74416E-005f, 4.88257E-005f, 3.05166E-005f, 1.22069E-005f, 3.59029E-006f, 9.39002E-007f, 2.37491E-007f, 5.95465E-008f, 1.48975E-008f},
		{3.05092E-005f, 3.04869E-005f, 3.03979E-005f, 3.00472E-005f, 2.87216E-005f, 2.44135E-005f, 1.52586E-005f, 6.10348E-006f, 1.79515E-006f, 4.69501E-007f, 1.18745E-007f, 2.97732E-008f, 7.44876E-009f},
		{1.52548E-005f, 1.52437E-005f, 1.51992E-005f, 1.50238E-005f, 1.4361E-005f, 1.22069E-005f, 7.62934E-006f, 3.05175E-006f, 8.97575E-007f, 2.34751E-007f, 5.93727E-008f, 1.48866E-008f, 3.72438E-009f},
		{7.62747E-006f, 7.62189E-006f, 7.59965E-006f, 7.51196E-006f, 7.18056E-006f, 6.10348E-006f, 3.81468E-006f, 1.52588E-006f, 4.48788E-007f, 1.17375E-007f, 2.96864E-008f, 7.44331E-009f, 1.86219E-009f},
		{3.81375E-006f, 3.81096E-006f, 3.79984E-006f, 3.756E-006f, 3.59029E-006f, 3.05175E-006f, 1.90734E-006f, 7.62939E-007f, 2.24394E-007f, 5.86876E-008f, 1.48432E-008f, 3.72166E-009f, 9.31095E-010f},
		{1.90688E-006f, 1.90548E-006f, 1.89992E-006f, 1.878E-006f, 1.79515E-006f, 1.52588E-006f, 9.53673E-007f, 3.8147E-007f, 1.12197E-007f, 2.93438E-008f, 7.42159E-009f, 1.86083E-009f, 4.65548E-010f},
		{9.53441E-007f, 9.52743E-007f, 9.49963E-007f, 9.39002E-007f, 8.97575E-007f, 7.62939E-007f, 4.76837E-007f, 1.90735E-007f, 5.60985E-008f, 1.46719E-008f, 3.71079E-009f, 9.30414E-010f, 2.32774E-010f},
		{4.76721E-007f, 4.76372E-007f, 4.74982E-007f, 4.69501E-007f, 4.48788E-007f, 3.8147E-007f, 2.38419E-007f, 9.53674E-008f, 2.80492E-008f, 7.33596E-009f, 1.8554E-009f, 4.65207E-010f, 1.16387E-010f},
		{2.3836E-007f, 2.38186E-007f, 2.37491E-007f, 2.34751E-007f, 2.24394E-007f, 1.90735E-007f, 1.19209E-007f, 4.76837E-008f, 1.40246E-008f, 3.66798E-009f, 9.27699E-010f, 2.32603E-010f, 5.81935E-011f},
		{1.1918E-007f, 1.19093E-007f, 1.18745E-007f, 1.17375E-007f, 1.12197E-007f, 9.53674E-008f, 5.96046E-008f, 2.38419E-008f, 7.01231E-009f, 1.83399E-009f, 4.63849E-010f, 1.16302E-010f, 2.90967E-011f}
	};
	/* table for Q_div values when no coupling */
	private static final float[] Q_div_tab = {
		0.0153846f, 0.030303f,
		0.0588235f, 0.111111f,
		0.2f, 0.333333f,
		0.5f, 0.666667f,
		0.8f, 0.888889f,
		0.941176f, 0.969697f,
		0.984615f, 0.992248f,
		0.996109f, 0.998051f,
		0.999024f, 0.999512f,
		0.999756f, 0.999878f,
		0.999939f, 0.999969f,
		0.999985f, 0.999992f,
		0.999996f, 0.999998f,
		0.999999f, 1f,
		1f, 1f,
		1f
	};

	private static final float[][] Q_div_tab_left = {
		{0.969704f, 0.888985f, 0.667532f, 0.336788f, 0.117241f, 0.037594f, 0.0153846f, 0.00967118f, 0.00823245f, 0.00787211f, 0.00778198f, 0.00775945f, 0.00775382f},
		{0.984619f, 0.94123f, 0.800623f, 0.503876f, 0.209877f, 0.0724638f, 0.030303f, 0.0191571f, 0.0163305f, 0.0156212f, 0.0154438f, 0.0153994f, 0.0153883f},
		{0.99225f, 0.969726f, 0.889273f, 0.670103f, 0.346939f, 0.135135f, 0.0588235f, 0.037594f, 0.0321361f, 0.0307619f, 0.0304178f, 0.0303317f, 0.0303102f},
		{0.99611f, 0.98463f, 0.941392f, 0.802469f, 0.515152f, 0.238095f, 0.111111f, 0.0724638f, 0.0622711f, 0.0596878f, 0.0590397f, 0.0588776f, 0.058837f},
		{0.998051f, 0.992256f, 0.969811f, 0.890411f, 0.68f, 0.384615f, 0.2f, 0.135135f, 0.117241f, 0.112652f, 0.111497f, 0.111208f, 0.111135f},
		{0.999025f, 0.996113f, 0.984674f, 0.942029f, 0.809524f, 0.555556f, 0.333333f, 0.238095f, 0.209877f, 0.202492f, 0.200625f, 0.200156f, 0.200039f},
		{0.999512f, 0.998053f, 0.992278f, 0.970149f, 0.894737f, 0.714286f, 0.5f, 0.384615f, 0.346939f, 0.336788f, 0.3342f, 0.33355f, 0.333388f},
		{0.999756f, 0.999025f, 0.996124f, 0.984848f, 0.944444f, 0.833333f, 0.666667f, 0.555556f, 0.515152f, 0.503876f, 0.500975f, 0.500244f, 0.500061f},
		{0.999878f, 0.999512f, 0.998058f, 0.992366f, 0.971429f, 0.909091f, 0.8f, 0.714286f, 0.68f, 0.670103f, 0.667532f, 0.666884f, 0.666721f},
		{0.999939f, 0.999756f, 0.999028f, 0.996169f, 0.985507f, 0.952381f, 0.888889f, 0.833333f, 0.809524f, 0.802469f, 0.800623f, 0.800156f, 0.800039f},
		{0.999969f, 0.999878f, 0.999514f, 0.998081f, 0.992701f, 0.97561f, 0.941176f, 0.909091f, 0.894737f, 0.890411f, 0.889273f, 0.888985f, 0.888913f},
		{0.999985f, 0.999939f, 0.999757f, 0.999039f, 0.996337f, 0.987654f, 0.969697f, 0.952381f, 0.944444f, 0.942029f, 0.941392f, 0.94123f, 0.94119f},
		{0.999992f, 0.99997f, 0.999878f, 0.999519f, 0.998165f, 0.993789f, 0.984615f, 0.97561f, 0.971429f, 0.970149f, 0.969811f, 0.969726f, 0.969704f},
		{0.999996f, 0.999985f, 0.999939f, 0.99976f, 0.999082f, 0.996885f, 0.992248f, 0.987654f, 0.985507f, 0.984848f, 0.984674f, 0.98463f, 0.984619f},
		{0.999998f, 0.999992f, 0.99997f, 0.99988f, 0.999541f, 0.99844f, 0.996109f, 0.993789f, 0.992701f, 0.992366f, 0.992278f, 0.992256f, 0.99225f},
		{0.999999f, 0.999996f, 0.999985f, 0.99994f, 0.99977f, 0.999219f, 0.998051f, 0.996885f, 0.996337f, 0.996169f, 0.996124f, 0.996113f, 0.99611f},
		{1f, 0.999998f, 0.999992f, 0.99997f, 0.999885f, 0.99961f, 0.999024f, 0.99844f, 0.998165f, 0.998081f, 0.998058f, 0.998053f, 0.998051f},
		{1f, 0.999999f, 0.999996f, 0.999985f, 0.999943f, 0.999805f, 0.999512f, 0.999219f, 0.999082f, 0.999039f, 0.999028f, 0.999025f, 0.999025f},
		{1f, 1f, 0.999998f, 0.999992f, 0.999971f, 0.999902f, 0.999756f, 0.99961f, 0.999541f, 0.999519f, 0.999514f, 0.999512f, 0.999512f},
		{1f, 1f, 0.999999f, 0.999996f, 0.999986f, 0.999951f, 0.999878f, 0.999805f, 0.99977f, 0.99976f, 0.999757f, 0.999756f, 0.999756f},
		{1f, 1f, 1f, 0.999998f, 0.999993f, 0.999976f, 0.999939f, 0.999902f, 0.999885f, 0.99988f, 0.999878f, 0.999878f, 0.999878f},
		{1f, 1f, 1f, 0.999999f, 0.999996f, 0.999988f, 0.999969f, 0.999951f, 0.999943f, 0.99994f, 0.999939f, 0.999939f, 0.999939f},
		{1f, 1f, 1f, 1f, 0.999998f, 0.999994f, 0.999985f, 0.999976f, 0.999971f, 0.99997f, 0.99997f, 0.99997f, 0.999969f},
		{1f, 1f, 1f, 1f, 0.999999f, 0.999997f, 0.999992f, 0.999988f, 0.999986f, 0.999985f, 0.999985f, 0.999985f, 0.999985f},
		{1f, 1f, 1f, 1f, 1f, 0.999998f, 0.999996f, 0.999994f, 0.999993f, 0.999992f, 0.999992f, 0.999992f, 0.999992f},
		{1f, 1f, 1f, 1f, 1f, 0.999999f, 0.999998f, 0.999997f, 0.999996f, 0.999996f, 0.999996f, 0.999996f, 0.999996f},
		{1f, 1f, 1f, 1f, 1f, 1f, 0.999999f, 0.999998f, 0.999998f, 0.999998f, 0.999998f, 0.999998f, 0.999998f},
		{1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.999999f, 0.999999f, 0.999999f, 0.999999f, 0.999999f, 0.999999f},
		{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f},
		{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f},
		{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f}
	};

	private static final float[][] Q_div_tab_right = {
		{0.00775382f, 0.00775945f, 0.00778198f, 0.00787211f, 0.00823245f, 0.00967118f, 0.0153846f, 0.037594f, 0.117241f, 0.336788f, 0.667532f, 0.888985f, 0.969704f},
		{0.0153883f, 0.0153994f, 0.0154438f, 0.0156212f, 0.0163305f, 0.0191571f, 0.030303f, 0.0724638f, 0.209877f, 0.503876f, 0.800623f, 0.94123f, 0.984619f},
		{0.0303102f, 0.0303317f, 0.0304178f, 0.0307619f, 0.0321361f, 0.037594f, 0.0588235f, 0.135135f, 0.346939f, 0.670103f, 0.889273f, 0.969726f, 0.99225f},
		{0.058837f, 0.0588776f, 0.0590397f, 0.0596878f, 0.0622711f, 0.0724638f, 0.111111f, 0.238095f, 0.515152f, 0.802469f, 0.941392f, 0.98463f, 0.99611f},
		{0.111135f, 0.111208f, 0.111497f, 0.112652f, 0.117241f, 0.135135f, 0.2f, 0.384615f, 0.68f, 0.890411f, 0.969811f, 0.992256f, 0.998051f},
		{0.200039f, 0.200156f, 0.200625f, 0.202492f, 0.209877f, 0.238095f, 0.333333f, 0.555556f, 0.809524f, 0.942029f, 0.984674f, 0.996113f, 0.999025f},
		{0.333388f, 0.33355f, 0.3342f, 0.336788f, 0.346939f, 0.384615f, 0.5f, 0.714286f, 0.894737f, 0.970149f, 0.992278f, 0.998053f, 0.999512f},
		{0.500061f, 0.500244f, 0.500975f, 0.503876f, 0.515152f, 0.555556f, 0.666667f, 0.833333f, 0.944444f, 0.984848f, 0.996124f, 0.999025f, 0.999756f},
		{0.666721f, 0.666884f, 0.667532f, 0.670103f, 0.68f, 0.714286f, 0.8f, 0.909091f, 0.971429f, 0.992366f, 0.998058f, 0.999512f, 0.999878f},
		{0.800039f, 0.800156f, 0.800623f, 0.802469f, 0.809524f, 0.833333f, 0.888889f, 0.952381f, 0.985507f, 0.996169f, 0.999028f, 0.999756f, 0.999939f},
		{0.888913f, 0.888985f, 0.889273f, 0.890411f, 0.894737f, 0.909091f, 0.941176f, 0.97561f, 0.992701f, 0.998081f, 0.999514f, 0.999878f, 0.999969f},
		{0.94119f, 0.94123f, 0.941392f, 0.942029f, 0.944444f, 0.952381f, 0.969697f, 0.987654f, 0.996337f, 0.999039f, 0.999757f, 0.999939f, 0.999985f},
		{0.969704f, 0.969726f, 0.969811f, 0.970149f, 0.971429f, 0.97561f, 0.984615f, 0.993789f, 0.998165f, 0.999519f, 0.999878f, 0.99997f, 0.999992f},
		{0.984619f, 0.98463f, 0.984674f, 0.984848f, 0.985507f, 0.987654f, 0.992248f, 0.996885f, 0.999082f, 0.99976f, 0.999939f, 0.999985f, 0.999996f},
		{0.99225f, 0.992256f, 0.992278f, 0.992366f, 0.992701f, 0.993789f, 0.996109f, 0.99844f, 0.999541f, 0.99988f, 0.99997f, 0.999992f, 0.999998f},
		{0.99611f, 0.996113f, 0.996124f, 0.996169f, 0.996337f, 0.996885f, 0.998051f, 0.999219f, 0.99977f, 0.99994f, 0.999985f, 0.999996f, 0.999999f},
		{0.998051f, 0.998053f, 0.998058f, 0.998081f, 0.998165f, 0.99844f, 0.999024f, 0.99961f, 0.999885f, 0.99997f, 0.999992f, 0.999998f, 1f},
		{0.999025f, 0.999025f, 0.999028f, 0.999039f, 0.999082f, 0.999219f, 0.999512f, 0.999805f, 0.999943f, 0.999985f, 0.999996f, 0.999999f, 1f},
		{0.999512f, 0.999512f, 0.999514f, 0.999519f, 0.999541f, 0.99961f, 0.999756f, 0.999902f, 0.999971f, 0.999992f, 0.999998f, 1f, 1f},
		{0.999756f, 0.999756f, 0.999757f, 0.99976f, 0.99977f, 0.999805f, 0.999878f, 0.999951f, 0.999986f, 0.999996f, 0.999999f, 1f, 1f},
		{0.999878f, 0.999878f, 0.999878f, 0.99988f, 0.999885f, 0.999902f, 0.999939f, 0.999976f, 0.999993f, 0.999998f, 1f, 1f, 1f},
		{0.999939f, 0.999939f, 0.999939f, 0.99994f, 0.999943f, 0.999951f, 0.999969f, 0.999988f, 0.999996f, 0.999999f, 1f, 1f, 1f},
		{0.999969f, 0.99997f, 0.99997f, 0.99997f, 0.999971f, 0.999976f, 0.999985f, 0.999994f, 0.999998f, 1f, 1f, 1f, 1f},
		{0.999985f, 0.999985f, 0.999985f, 0.999985f, 0.999986f, 0.999988f, 0.999992f, 0.999997f, 0.999999f, 1f, 1f, 1f, 1f},
		{0.999992f, 0.999992f, 0.999992f, 0.999992f, 0.999993f, 0.999994f, 0.999996f, 0.999998f, 1f, 1f, 1f, 1f, 1f},
		{0.999996f, 0.999996f, 0.999996f, 0.999996f, 0.999996f, 0.999997f, 0.999998f, 0.999999f, 1f, 1f, 1f, 1f, 1f},
		{0.999998f, 0.999998f, 0.999998f, 0.999998f, 0.999998f, 0.999998f, 0.999999f, 1f, 1f, 1f, 1f, 1f, 1f},
		{0.999999f, 0.999999f, 0.999999f, 0.999999f, 0.999999f, 0.999999f, 1f, 1f, 1f, 1f, 1f, 1f, 1f},
		{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f},
		{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f},
		{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f}
	};

	public static void extract_envelope_data(SBR sbr, Channel ch) {

		for(int l = 0; l<ch.L_E; l++) {
			if(ch.bs_df_env[l]==0) {
				for(int k = 1; k<sbr.n[ch.f[l]]; k++) {
					ch.E[k][l] = ch.E[k-1][l]+ch.E[k][l];
					if(ch.E[k][l]<0)
						ch.E[k][l] = 0;
				}

			}
			else { /* bs_df_env == 1 */

				int g = (l==0) ? ch.f_prev : ch.f[l-1];
				int E_prev;

				if(ch.f[l]==g) {
					for(int k = 0; k<sbr.n[ch.f[l]]; k++) {
						if(l==0)
							E_prev = ch.E_prev[k];
						else
							E_prev = ch.E[k][l-1];

						ch.E[k][l] = E_prev+ch.E[k][l];
					}

				}
				else if((g==1)&&(ch.f[l]==0)) {

					for(int k = 0; k<sbr.n[ch.f[l]]; k++) {
						for(int i = 0; i<sbr.N_high; i++) {
							if(sbr.f_table_res[FBT.HI_RES][i]==sbr.f_table_res[FBT.LO_RES][k]) {
								if(l==0)
									E_prev = ch.E_prev[i];
								else
									E_prev = ch.E[i][l-1];

								ch.E[k][l] = E_prev+ch.E[k][l];
							}
						}
					}

				}
				else if((g==0)&&(ch.f[l]==1)) {

					for(int k = 0; k<sbr.n[ch.f[l]]; k++) {
						for(int i = 0; i<sbr.N_low; i++) {
							if((sbr.f_table_res[FBT.LO_RES][i]<=sbr.f_table_res[FBT.HI_RES][k])
								&&(sbr.f_table_res[FBT.HI_RES][k]<sbr.f_table_res[FBT.LO_RES][i+1])) {
								if(l==0)
									E_prev = ch.E_prev[i];
								else
									E_prev = ch.E[i][l-1];

								ch.E[k][l] = E_prev+ch.E[k][l];
							}
						}
					}
				}
			}
		}
	}

	public static void extract_noise_floor_data(SBR sbr, Channel ch) {

		for(int l = 0; l<ch.L_Q; l++) {
			if(ch.bs_df_noise[l]==0) {
				for(int k = 1; k<sbr.N_Q; k++) {
					ch.Q[k][l] = ch.Q[k][l]+ch.Q[k-1][l];
				}
			}
			else {
				if(l==0) {
					for(int k = 0; k<sbr.N_Q; k++) {
						ch.Q[k][l] = ch.Q_prev[k]+ch.Q[k][0];
					}
				}
				else {
					for(int k = 0; k<sbr.N_Q; k++) {
						ch.Q[k][l] = ch.Q[k][l-1]+ch.Q[k][l];
					}
				}
			}
		}
	}

	/* calculates 1/(1+Q) */
	/* [0..1] */
	public static float calc_Q_div(SBR2 sbr, Channel ch, int m, int l) {
		if(sbr.bs_coupling) {
			/* left channel */
			if((sbr.ch0.Q[m][l]<0||sbr.ch0.Q[m][l]>30)
				||(sbr.ch1.Q[m][l]<0||sbr.ch1.Q[m][l]>24 /* 2*panOffset(1) */)) {
				return 0;
			}
			else {
				/* the pan parameter is always even */
				if(ch==sbr.ch0) {
					return Q_div_tab_left[sbr.ch0.Q[m][l]][sbr.ch1.Q[m][l]>>1];
				}
				else {
					return Q_div_tab_right[sbr.ch0.Q[m][l]][sbr.ch1.Q[m][l]>>1];
				}
			}
		}
		else {
			return calc_Q_div(ch, m, l);
		}
	}

	public static float calc_Q_div(Channel ch, int m, int l) {
		/* no coupling */
		if(ch.Q[m][l]<0||ch.Q[m][l]>30) {
			return 0;
		}
		else {
			return Q_div_tab[ch.Q[m][l]];
		}
	}

	/* calculates Q/(1+Q) */
	/* [0..1] */
	public static float calc_Q_div2(SBR2 sbr, Channel ch, int m, int l) {
		if(sbr.bs_coupling) {
			if((sbr.ch0.Q[m][l]<0||sbr.ch0.Q[m][l]>30)
				||(sbr.ch1.Q[m][l]<0||sbr.ch1.Q[m][l]>24 /* 2*panOffset(1) */)) {
				return 0;
			}
			else {
				/* the pan parameter is always even */
				if(ch==sbr.ch0) {
					return Q_div2_tab_left[sbr.ch0.Q[m][l]][sbr.ch1.Q[m][l]>>1];
				}
				else {
					return Q_div2_tab_right[sbr.ch0.Q[m][l]][sbr.ch1.Q[m][l]>>1];
				}
			}
		}
		else {
			return calc_Q_div2(ch, m, l);
		}
	}

	public static float calc_Q_div2(Channel ch, int m, int l) {
		/* no coupling */
		if(ch.Q[m][l]<0||ch.Q[m][l]>30) {
			return 0;
		}
		else {
			return Q_div2_tab[ch.Q[m][l]];
		}
	}

	public static void dequantChannel(SBR sbr, Channel ch) {
		int amp = (ch.amp_res) ? 0 : 1;

		for(int l = 0; l<ch.L_E; l++) {
			for(int k = 0; k<sbr.n[ch.f[l]]; k++) {
				/* +6 for the *64 and -10 for the /32 in the synthesis QMF (fixed)
				 * since this is a energy value: (x/32)^2 = (x^2)/1024
				 */
				/* exp = (ch.E[k][l] >> amp) + 6; */
				int exp = (ch.E[k][l]>>amp);

				if((exp<0)||(exp>=64)) {
					ch.E_orig[k][l] = 0;
				}
				else {
					ch.E_orig[k][l] = E_deq_tab[exp];

					/* save half the table size at the cost of 1 multiply */
					if(amp!=0&&(ch.E[k][l]&1)!=0) {
						ch.E_orig[k][l] = (ch.E_orig[k][l]*1.414213562f);
					}
				}
			}
		}

		for(int l = 0; l<ch.L_Q; l++) {
			for(int k = 0; k<sbr.N_Q; k++) {
				ch.Q_div[k][l] = calc_Q_div(ch, k, l);
				ch.Q_div2[k][l] = calc_Q_div2(ch, k, l);
			}
		}
	}

	private static final float[] E_pan_tab = {
		0.000244081f, 0.000488043f,
		0.00097561f, 0.00194932f,
		0.00389105f, 0.00775194f,
		0.0153846f, 0.030303f,
		0.0588235f, 0.111111f,
		0.2f, 0.333333f,
		0.5f, 0.666667f,
		0.8f, 0.888889f,
		0.941176f, 0.969697f,
		0.984615f, 0.992248f,
		0.996109f, 0.998051f,
		0.999024f, 0.999512f,
		0.999756f
	};

	public static void unmap(SBR2 sbr) {

		int amp0 = (sbr.ch0.amp_res) ? 0 : 1;
		int amp1 = (sbr.ch1.amp_res) ? 0 : 1;

		for(int l = 0; l<sbr.ch0.L_E; l++) {
			for(int k = 0; k<sbr.n[sbr.ch0.f[l]]; k++) {
				/* +6: * 64 ; +1: * 2 ; */
				int exp0 = (sbr.ch0.E[k][l]>>amp0)+1;

				/* UN_MAP removed: (x / 4096) same as (x >> 12) */
				/* E[1] is always even so no need for compensating the divide by 2 with
				 * an extra multiplication
				 */
				/* exp1 = (sbr.ch1.E[k][l] >> amp1) - 12; */
				int exp1 = (sbr.ch1.E[k][l]>>amp1);

				if((exp0<0)||(exp0>=64)
					||(exp1<0)||(exp1>24)) {
					sbr.ch1.E_orig[k][l] = 0;
					sbr.ch0.E_orig[k][l] = 0;
				}
				else {
					float tmp = E_deq_tab[exp0];
					if(amp0!=0&&(sbr.ch0.E[k][l]&1)!=0) {
						tmp *= 1.414213562;
					}

					/* panning */
					sbr.ch0.E_orig[k][l] = (tmp*E_pan_tab[exp1]);
					sbr.ch1.E_orig[k][l] = (tmp*E_pan_tab[24-exp1]);
				}
			}
		}

		for(int l = 0; l<sbr.ch0.L_Q; l++) {
			for(int k = 0; k<sbr.N_Q; k++) {
				sbr.ch0.Q_div[k][l] = calc_Q_div(sbr, sbr.ch0, k, l);
				sbr.ch1.Q_div[k][l] = calc_Q_div(sbr, sbr.ch1, k, l);
				sbr.ch0.Q_div2[k][l] = calc_Q_div2(sbr, sbr.ch0, k, l);
				sbr.ch1.Q_div2[k][l] = calc_Q_div2(sbr, sbr.ch1, k, l);
			}
		}
	}
}
