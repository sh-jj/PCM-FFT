package pcmfft;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * Created by jiangyi on 04/06/2017.
 */

class Util {

    static int calculateN(int FS, double duration) {
        return (int) (FS * duration);
    }

    static void addSin(short[] sound, int offset, int length, int frequency, int FS, int volume) {
        double omega = 2 * Math.PI * frequency;
        double step = 1.0f / FS;
        for (int i = 0; i < length; i++) {
            sound[i + offset] += (short) (volume * Math.sin(omega * i * step));
        }
    }

    static short[] generateSin(int frequency, double duration, int FS, int volume) {
        int N = calculateN(FS, duration);
        short[] sound = new short[N];
        if (volume <= 0) {
            for (int i = 0; i < N; i++) sound[i] = 0;
            return sound;
        }
        double omega = 2 * Math.PI * frequency;
        double step = 1.0f / FS;
        for (int i = 0; i < N; i++) {
            sound[i] = (short) (volume * Math.sin(omega * i * step));
        }
        return sound;
    }

    static void addSlopeFrequency(short[] sound, int offset, int length,
                                  int frequency1, int frequency2, int FS, int volume) {
        double step = 1.0f / FS;
        int fgap = frequency2 - frequency1;
        double omega;
        int frequency;
        for (int i = 0; i < length; i++) {
            frequency = frequency1 + fgap * i / (length - 1);
            omega = 2 * Math.PI * frequency;
            sound[i + offset] += (short) (volume * Math.sin(omega * i * step));
        }
    }

    static short[] generateSlopeFrequency(int frequency1, int frequency2, double duration, int FS, int volume) {
        short[] res = Util.generateSin(frequency1, duration, FS, 0);
        Util.addSlopeFrequency(res, 0, res.length, frequency1, frequency2, FS, volume);
        return res;
    }

    static void addCrossFrequency(short[] sound, int offset, int length,
                                  int frequency1, int frequency2, int FS, int volume) {
        addSlopeFrequency(sound, offset, length, frequency1, frequency2, FS, volume / 2);
        addSlopeFrequency(sound, offset, length, frequency2, frequency1, FS, volume / 2);
    }

    static short[] generateCrossFrequency(int frequency1, int frequency2, double duration, int FS, int volume) {
        short[] res = Util.generateSin(frequency2, duration, FS, 0);
        Util.addCrossFrequency(res, 0, res.length, frequency1, frequency2, FS, volume);
        return res;
    }

    static short average(short[] sound, int begin, int end, int gap) {
        long sum = 0;
        for (int i = begin; i < end; i = i + gap) {
            sum += sound[i];
        }
        return ((short) (sum / (end - begin)));
    }

    static double covariationValue(short[] record, short[] preamble, int pos, double preambleAbs, int gap) {
        int len = preamble.length;
        long res = 0;
        long recordAbsSquare = 0;
        short recordAverage = average(record, pos, pos + len, gap);
        int normRecordV;
        for (int i = 0; i < len; i = i + gap) {
            normRecordV = record[pos + i] - recordAverage;
            res += normRecordV * preamble[i];
            recordAbsSquare += normRecordV * normRecordV;
        }
        double recordAbs = Math.sqrt((double) recordAbsSquare);
        return res / (recordAbs * preambleAbs);
    }

    static double absolute(short[] sound, int gap) {
        long square = 0;
        int N = sound.length;
        for (int i = 0; i < N; i = i + gap) {
            square += sound[i] * sound[i];
        }
        return Math.sqrt(((double) square));
    }

    static int findPreambleBegin(short[] record, short[] preamble, int begin, int end, int gap) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        int maxIndex = begin;
        int minIndex = begin;
        double preambleAbs = absolute(preamble, gap);
        double cov;
        for (int i = begin; i <= end; i++) {
            cov = covariationValue(record, preamble, i, preambleAbs, gap);
            if (cov > max) {
                max = cov;
                maxIndex = i;
            }
            if (cov < min) {
                min = cov;
                minIndex = i;
            }
        }
        return maxIndex;
    }

    static int calculateRange(short[] sound, int offset, int N) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int endPos = offset + N;
        for (int i = offset; i < endPos; i++) {
            if (sound[i] > max) {
                max = sound[i];
            }
            if (sound[i] < min) {
                min = sound[i];
            }
        }
        return (max - min);
    }

    static void writeRecordFile(FileWriter fileWriter, int value, short[] sound) {
        if (fileWriter == null) return;
        try {
            fileWriter.write(String.format("%d\n", value));
            for (short i : sound) {
                fileWriter.write(String.format("%d ", i));
            }
            fileWriter.write('\n');
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void writeRecordFile(FileWriter fileWriter, double[][] sys) {
        if (fileWriter == null) return;
        try {
            for (double[] s : sys) {
                for (double d : s)
                    fileWriter.write(String.format("%f ", d));
                fileWriter.write('\n');
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int msb(int number) {
        int res = 1;
        while ((number = number >> 1) > 0) {
            res = res << 1;
        }
        return res;
    }

    static int closestLargerPower2(int number) {
        return msb(number) << 1;
    }

    static double[] soundToDoubleArray(short[] sound, int offset, int length) {
        int extendLen = closestLargerPower2(length);
        double[] res = new double[extendLen];
        int i = 0;
        for (; i < length; i++) {
            res[i] = (double) sound[i + offset];
        }
        for (; i < extendLen; i++) {
            res[i] = 0;
        }
        return res;
    }

    static double[] soundToDoubleArray(short[] sound) {
        return soundToDoubleArray(sound, 0, sound.length);
    }

    static short[] doubleArrayToSound(double[] array) {
        int length = array.length;
        short[] sound = new short[length];
        for (int i = 0; i < length; i++) {
            sound[i] = (short) array[i];
        }
        return sound;
    }

    static void checkRIFormat(double[][] x, double[][] y) {
        if (x == null)
            throw new NullPointerException("the first parameter is null!");
        if (y == null)
            throw new NullPointerException("the second parameter is null!");
        if (x.length != 2)
            throw new DimensionMismatchException(x.length, 2);
        if (y.length != 2)
            throw new DimensionMismatchException(y.length, 2);
        if (x[0].length != y[0].length)
            throw new DimensionMismatchException(x[0].length, y[0].length);
    }

    static void addTo(double[][] origin, double[][] plus) {
        checkRIFormat(origin, plus);
        for (int i = 0; i < origin.length; ++i) {
            for (int j = 0; j < origin[0].length; ++j) {
                origin[i][j] += plus[i][j];
            }
        }
    }

    static double[][] multiply(double[][] x, double[][] y, boolean check) {
        if (check) checkRIFormat(x, y);
        int loopLen, storeLen;
        if (check)
            loopLen = storeLen = x[0].length;
        else {
            storeLen = max(x[0].length, y[0].length);
            loopLen = min(x[0].length, y[0].length);
        }

        double[] resR = new double[storeLen];
        double[] resI = new double[storeLen];
        for (int i = 0; i < loopLen; i++) {
            resR[i] = x[0][i] * y[0][i] - x[1][i] * y[1][i];
            resI[i] = x[0][i] * y[1][i] + x[1][i] * y[0][i];
        }
        return new double[][]{resR, resI};
    }

    static double[][] scaleMultiply(double[][] sound, double[][] sys) {
        int soundLen = sound[0].length, sysLen = sys[0].length;
        double[] resR = new double[soundLen];
        double[] resI = new double[soundLen];
        int i_sys;
        double alpha;
        for (int i = 0; i < soundLen; i++) {
            alpha = ((double) i) / soundLen;
            i_sys = ((int) (sysLen * alpha));
            resR[i] = sound[0][i] * sys[0][i_sys] - sound[1][i] * sys[1][i_sys];
            resI[i] = sound[0][i] * sys[1][i_sys] + sound[1][i] * sys[0][i_sys];
        }
        return new double[][]{resR, resI};
    }

    private static int max(int a, int b) {
        return a > b ? a : b;
    }

    private static int min(int a, int b) {
        return a < b ? a : b;
    }

    // x / y
    static double[][] divide(double[][] x, double[][] y) {
        checkRIFormat(x, y);
        int length = x[0].length;
        double[] resR = new double[length];
        double[] resI = new double[length];
        for (int i = 0; i < length; i++) {
            if (y[0][i] == 0 && y[1][i] == 0) {
                resR[i] = 0;
                resI[i] = 0;
                continue;
            }
            resR[i] = (x[0][i] * y[0][i] + x[1][i] * y[1][i]) /
                    (y[0][i] * y[0][i] + y[1][i] * y[1][i]);
            resI[i] = (x[1][i] * y[0][i] - x[0][i] * y[1][i]) /
                    (y[0][i] * y[0][i] + y[1][i] * y[1][i]);
        }
        return new double[][]{resR, resI};
    }

    static void divideOn(double[][] dividend, int divider) {
        for (int i = 0; i < dividend.length; ++i) {
            for (int j = 0; j < dividend[i].length; ++j) {
                dividend[i][j] /= divider;
            }
        }
    }


    static double[] calculateSound(short[] sound, double[][] sys) {
        int soundN = sound.length;
        int powerN = closestLargerPower2(soundN);
        double[][] soundRI = new double[][]{
                soundToDoubleArray(sound),
                new double[powerN]
        };
        fft(soundRI);
        // double[][] calc = multiply(soundRI, sys, true);
        double[][] calc = scaleMultiply(soundRI, sys);
        ifft(calc);
        return calc[0];
    }

    static void fft(double[][] dataRI) {
        FastFourierTransformer.transformInPlace(dataRI,
                DftNormalization.STANDARD, TransformType.FORWARD);
    }

    static void ifft(double[][] dataRI) {
        FastFourierTransformer.transformInPlace(dataRI,
                DftNormalization.STANDARD, TransformType.INVERSE);
    }

}