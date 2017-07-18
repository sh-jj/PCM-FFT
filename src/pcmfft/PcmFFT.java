
package pcmfft;

import java.io.*;

public class PcmFFT {

	public static void main(String[] args) {
		int frequency = 44100;
		try {
		//File file = new File("reverseme.pcm");

		File file = new File("reverseme_recorded.pcm");
		
        DataInputStream inputStream_org = new DataInputStream(new FileInputStream(file));
        int soundN = frequency;
        int powerN = Util.closestLargerPower2(soundN);

            short[] buffer1 = new short[soundN];
            for (int i = 0; i<buffer1.length;i++){
                buffer1[i] = inputStream_org.readShort();
			}
		inputStream_org.close();
			double[][] soundRI = new double[][]{
				Util.soundToDoubleArray(buffer1),new double[powerN]
			};
			
            Util.fft(soundRI);
            
            //FileWriter  out = new FileWriter(new File("frequency.txt"));

            FileWriter  out = new FileWriter(new File("frequency_recorded.txt"));
            
            System.out.print(soundRI[0][0]);
            System.out.print(' ');
            System.out.print(soundRI[1][0]);
            for (int i=0; i <soundRI[0].length;i++)
            {
            	out.write(String.valueOf(soundRI[0][i])+'\n');
            	//out.write(String.valueOf(soundRI[0][i])+' '+String.valueOf(soundRI[1][i])+'\n');
            }
            out.close();
		}
		catch (IOException e) {
			System.out.print("ayayayayayayayayaayayay");
		}
	}

}
