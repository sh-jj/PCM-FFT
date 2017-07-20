
package pcmfft;

import java.io.*;

public class PcmFFT {

	public static void main(String[] args) {
		
		for (int tt=1;tt<=3;tt++)
		{
		
			int frequency = 44100;
			try {
					File file1 = new File("reverseme"+String.valueOf(tt)+".pcm");
					File file2 = new File("reverseme_recorded"+String.valueOf(tt)+".pcm");
		
					DataInputStream inputStream_org = new DataInputStream(new FileInputStream(file1));
					int soundN = frequency;
					int powerN = Util.closestLargerPower2(soundN);

					boolean P1=false,P2=false;
					short[] buffer1 = new short[soundN];
					
					for (int i = 0; i<buffer1.length;i++){
						//buffer1[i] = inputStream_org.readShort();
						if (P1) buffer1[i] = inputStream_org.readShort();
						else {
								int label=0;
								short tmp=inputStream_org.readShort();
								for (;tmp==0;)
								{
									tmp=inputStream_org.readShort();
									label++;
								}
								P1=true;
								buffer1[i]=tmp;
								System.out.print(label);
								System.out.print(' ');
						}
					}
					inputStream_org.close();
		
					inputStream_org = new DataInputStream(new FileInputStream(file2));
					soundN = frequency; 
					powerN = Util.closestLargerPower2(soundN);
					short[] buffer2 = new short[soundN];
					for (int i = 0; i<buffer2.length;i++){
							//buffer2[i] = inputStream_org.readShort();
							if (P2) buffer2[i] = inputStream_org.readShort();
							else {
									int label =0;
									short tmp=inputStream_org.readShort();
									for (;tmp==0;)
									{
										tmp=inputStream_org.readShort();
										label++;
									}
									P2=true;
									buffer2[i]=tmp;
									System.out.print(label);
									System.out.print(' ');
							}
						}
					inputStream_org.close();
		

					FileWriter  soundout1 = new FileWriter(new File("sound"+String.valueOf(tt)+".txt"));
					FileWriter  soundout2 = new FileWriter(new File("sound_recorded"+String.valueOf(tt)+".txt"));
					for (int i=0;i<buffer1.length;i++)
					{
						soundout1.write(String.valueOf(buffer1[i])+'\n');
					}
		
					for (int i=0;i<buffer2.length;i++)
					{
						soundout2.write(String.valueOf(buffer2[i])+'\n');
					}
        
					soundout1.close();
					soundout2.close();
		
		
		
					double[][] soundRI1 = new double[][]{
						Util.soundToDoubleArray(buffer1),new double[powerN]
					};
					Util.fft(soundRI1);
        
					double[][] soundRI2 = new double[][]{
						Util.soundToDoubleArray(buffer2),new double[powerN]
					};
					Util.fft(soundRI2);
        

            
					FileWriter  frequencyout1 = new FileWriter(new File("frequency"+String.valueOf(tt)+".txt"));
					FileWriter  frequencyout2 = new FileWriter(new File("frequency_recorded"+String.valueOf(tt)+".txt"));
            
            
					//System.out.print(soundRI[0][0]);
					//System.out.print(' ');
					//System.out.print(soundRI[1][0]);
					
					for (int i=0; i <soundRI1[0].length;i++)
					{
						frequencyout1.write(String.valueOf(soundRI1[0][i])+'\n');
						//out.write(String.valueOf(soundRI[0][i])+' '+String.valueOf(soundRI[1][i])+'\n');
					}
					frequencyout1.close();
        
					for (int i=0; i <soundRI2[0].length;i++)
					{
						frequencyout2.write(String.valueOf(soundRI2[0][i])+'\n');
						//out.write(String.valueOf(soundRI[0][i])+' '+String.valueOf(soundRI[1][i])+'\n');
					}
					frequencyout2.close();
        
			}
			catch (IOException e) {
				System.out.print("ayayayayayayayayaayayay"+String.valueOf(tt));
			}
			finally {
				System.out.println("finish");
			}
		}
	}

}
