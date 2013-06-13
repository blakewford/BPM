main()
{
	short* k;
	k=0x10000;
	for (k; k<0x20000; k++) {
		*k=0x91;
	}
	return;
}
