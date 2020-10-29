package org.ounl.noisereporter.feeback;


public class FeedbackColorFactory {

	public static int COLOR_GRADIENT_SIZE = 512;
	private static int COLOR_GRADIENT_HALF_SIZE = 256;

	private FeedbackColor[] mColorGradientArray = new FeedbackColor[COLOR_GRADIENT_SIZE];

	public FeedbackColorFactory() {

		for (int i = 0; i < (COLOR_GRADIENT_SIZE / 2); i++) {

			// From green to yellow
			mColorGradientArray[i] = new FeedbackColor(i, 255, 0);

			// From yellow to red
			mColorGradientArray[i + COLOR_GRADIENT_HALF_SIZE] = new FeedbackColor(
					255, 255 - i, 0);

		}

	}

	/**
	 * Returns color for given index from 1 to 512
	 * 
	 * @param index
	 * @return
	 */
	public FeedbackColor getColor(int index) {

		if(index >= 512){
			return mColorGradientArray[index-1];
		}
		
		return mColorGradientArray[index];
	}

}
