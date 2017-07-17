/**
 * 
 */
package pcmfft;

import java.io.*;

/**
 * @author john
 *
 */
public class PcmFFT {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		int frequency = 44100;
		try {
		File file = new File("reverseme.pcm");
		
        DataInputStream inputStream_org = new DataInputStream(new FileInputStream(file));
        int soundN = frequency;
        int powerN = Util.closestLargerPower2(soundN);

            short[] buffer1 = new short[soundN];
            for (int i = 0; i<buffer1.length;i++){
                buffer1[i] = inputStream_org.readShort();
			}
				
			double[][] soundRI = new double[][]{
				Util.soundToDoubleArray(buffer1),new double[powerN]
			};
			
            Util.fft(soundRI);
		}
		catch (IOException e) {}
	}

}
