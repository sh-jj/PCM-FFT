
package pcmfft;

import java.util.Scanner;
import java.io.*;

public class PcmFFT {
	
	static int frequency = 44100;
	static int soundN = frequency * 5;// 5s
	static int Length = Util.closestLargerPower2( frequency * 5 );		//5s

	
	
	
	public static short[] changePcmtoSound(String Path) {
		short [] sound = new short [soundN];
		try {
				File filepoint = new File(Path);
				DataInputStream inData = new DataInputStream(new FileInputStream(filepoint));
				for (int i=0;i<soundN;i++)
				{
					try {
							sound[i] = inData.readShort();
							
					} catch (IOException e)
					{
						System.out.println("No more data");
						break;
					}
				}
				inData.close();
		}	catch (IOException e)
		{
			System.out.println("where is the file: "+Path);
		}
		
		
		
		return sound;
	}
	
	public static void outSound(String Path, short [] sound)
	{
		try {
			FileWriter  soundout = new FileWriter(new File(Path));
			for (int i=0;i<sound.length;i++)
			{
				soundout.write(String.valueOf(sound[i])+'\n');
			}
		
			soundout.close();
		} catch (IOException e)
		{
			System.out.println("can't output to file: "+Path);
		}
	}
	
	public static void outFrequency(String Path, double [][] frequency)
	{
		try {
			
			FileWriter  frequencyout = new FileWriter(new File(Path));
			
			for (int i=0; i <frequency[0].length;i++)
			{
				//frequencyout1.write(String.valueOf(soundRI1[0][i])+'\n');
				frequencyout.write(String.valueOf(frequency[0][i])+' '+String.valueOf(frequency[1][i])+'\n');
				
			}
			
			frequencyout.close();
		} catch (IOException e)
		{
			System.out.println("can't output to file: "+Path);
		}
	}
	
	public static void main(String[] args) {
		
		 
		double[][][] Uin = new double[4][2][524288];
		double[][][] Vin = new double[4][2][524288];
		
		for (int tt=1;tt<=3;tt++)
		{
		
			
			
				
					short[] buffer1 = changePcmtoSound("reverseme"+String.valueOf(tt)+".pcm");
					short[] buffer2 = changePcmtoSound("reverseme_recorded"+String.valueOf(tt)+".pcm");
		
					
					outSound("sound"+String.valueOf(tt)+".txt",buffer1);
					outSound("sound_recorded"+String.valueOf(tt)+".txt",buffer2);
					
					
					double[][] soundRI1 = new double[][]{
						Util.soundToDoubleArray(buffer1),new double[Length]
					};
					Util.fft(soundRI1);
        
					double[][] soundRI2 = new double[][]{
						Util.soundToDoubleArray(buffer2),new double[Length]
					};
					Util.fft(soundRI2);
        
					
					outFrequency("frequency"+String.valueOf(tt)+".txt",soundRI1);
					outFrequency("frequency"+String.valueOf(tt)+".txt",soundRI2);
            		
					Uin[tt] = soundRI1;
					Vin[tt] = soundRI2;
					
					
					double [][] sys=Util.divide(soundRI2,soundRI1);
					
					outFrequency("sys_res"+String.valueOf(tt)+".txt",sys);
					
					
					 System.out.println("fft-finish");
		}
		for (int u=1;u<=3;u++)
			for (int v=1;v<=3;v++)
				try {
						
						Scanner uin = new Scanner(new FileInputStream("frequency"+String.valueOf(u)+".txt"));
						Scanner vin = new Scanner(new FileInputStream("frequency_recorded"+String.valueOf(v)+".txt"));
						
						int Length = 65536;
						
						double[][] freU = new double[2][Length];
						
						for (int i=0; i < Length; i++)
						{
							freU[0][i] = uin.nextDouble();
							freU[1][i] = uin.nextDouble();
						}
						
						double[][] freV = new double[2][Length];
						
						for (int i=0; i < Length; i++)
						{
							freV[0][i] = vin.nextDouble();
							freV[1][i] = vin.nextDouble();
						}
						
						//double [][] sys=Util.divide(freV,freU);
						
						double [][] sys=Util.divide(Uin[v],Vin[u]);
						
						
						FileWriter  sysout = new FileWriter(new File("sys_res"+String.valueOf(u)+String.valueOf(v)+".txt"));
						for (int i=0;i<sys[0].length;i++)
						{
							sysout.write(String.valueOf(sys[0][i])+' '+String.valueOf(sys[1][i])+'\n');
						}
						sysout.close();
						
						
				}
				catch (IOException e) {
					System.out.print("ayayayayayayayayaayayay"+String.valueOf(u)+" "+String.valueOf(v));
				}
				finally {
					System.out.println("sys-finish");
				}
		}

}
