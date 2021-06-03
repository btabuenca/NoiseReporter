package org.ounl.noisereporter.prisma.config;


public class ColorFactory {

	public static int COLOR_GRADIENT_SIZE = 512;
	private static int COLOR_GRADIENT_HALF_SIZE = 256;

	private Color[] mColorGradientArray = new Color[COLOR_GRADIENT_SIZE];

	public ColorFactory() {

		for (int i = 0; i < (COLOR_GRADIENT_SIZE / 2); i++) {

			// From green to yellow
			mColorGradientArray[i] = new Color(i, 255, 0);

			// From yellow to red
			mColorGradientArray[i + COLOR_GRADIENT_HALF_SIZE] = new Color(
					255, 255 - i, 0);

		}

	}

	/**
	 * Returns color for given index from 1 to 512
	 * 
	 * @param index
	 * @return
	 */
	public Color getColor(int index) {

		if(index >= 512){
			return mColorGradientArray[index-1];
		}
		
		return mColorGradientArray[index];
	}

}
