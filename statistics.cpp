//
#include <iostream>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <algorithm>

char str[]="sys_res00.txt";
const int Length = 65536;

double Sys[5][5][2][Length];
double Sys_res[2][Length];

using namespace std;

int main()
{
	for (int u=1;u<=3;u++)
		for (int v=1;v<=3;v++)
		{
			str[7]='0'+u;
			str[8]='0'+v;
			
			FILE* In = fopen(str,"r");
			
			for (int i = 0; i < Length; i++)
			{
				fscanf(In,"%lf",&Sys[u][v][0][i]);
				fscanf(In,"%lf",&Sys[u][v][1][i]);
			}
			fclose(In);
		}
	for (int i = 0; i < Length; i++)
	{
		int tot=0;
		double tmp[30];
		for (int u=1;u<=3;u++)
			for (int v=1;v<=3;v++)
			{
				tmp[++tot]=Sys[u][v][0][i];
				
				if (u==v)
				{
					tmp[++tot]=Sys[u][v][0][i];
					tmp[++tot]=Sys[u][v][0][i];
				}
				
				sort(tmp+1,tmp+tot+1);
			}
		double sum=0;
		for (int j=3;j<=tot-2;j++) sum+=tmp[j];
		
		Sys_res[0][i]=sum/(tot-4);
		
		
		tot=0;
		for (int u=1;u<=3;u++)
			for (int v=1;v<=3;v++)
			{
				tmp[++tot]=Sys[u][v][1][i];
				
				if (u==v)
				{
					tmp[++tot]=Sys[u][v][1][i];
					tmp[++tot]=Sys[u][v][1][i];
				}
				
				
				sort(tmp+1,tmp+tot+1);
			}
		sum=0;
		for (int j=3;j<=tot-2;j++) sum+=tmp[j];
		
		Sys_res[1][i]=sum/(tot-4);
		
	}
	
	FILE* sysout = fopen("Sys_made.txt","w");
	for (int i=0;i<Length;i++)
		fprintf(sysout,"%.18lf %.18lf\n",Sys_res[0][i],Sys_res[1][i]);
	
	
	
}