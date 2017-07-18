#include <iostream>



const int Len=65536;
const int mini_len=10;

//get the average of per mini_len frequency

double frequency[Len*2],frequency_recorded[Len*2];
double divided[Len*2],divided_recorded[Len*2];


int main()
{
	FILE* filein_1=fopen("frequency.txt","r");
	FILE* filein_2=fopen("frequency_recorded.txt","r");
	
	
	for (int i=0;i<Len;i++)	fscanf(filein_1,"%lf",&frequency[i]);
	
	for (int i=0;i<Len;i++)	fscanf(filein_2,"%lf",&frequency_recorded[i]);
	
	for (int i=0;i<Len/mini_len;i++)
	{
		divided[i]=0;
		for (int j=0;j<mini_len;j++)
			divided[i]+=frequency[i*mini_len+j];
		divided[i]/=mini_len;
	}
	
	for (int i=0;i<Len/mini_len;i++)
	{
		divided_recorded[i]=0;
		for (int j=0;j<mini_len;j++)
			divided_recorded[i]+=frequency_recorded[i*mini_len+j];
		divided_recorded[i]/=mini_len;
	}
	
	FILE* fileout_1=fopen("frequency_divided.txt","w");
	FILE* fileout_2=fopen("frequency_recorded_divided.txt","w");
	
	for (int i=0;i<Len/mini_len;i++)
	{
		fprintf(fileout_1,"%lf\n",divided[i]);
		fprintf(fileout_2,"%lf\n",divided_recorded[i]);
	}
	
}