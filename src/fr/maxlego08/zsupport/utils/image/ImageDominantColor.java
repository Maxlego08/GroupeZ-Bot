package fr.maxlego08.zsupport.utils.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author jittagornp
 *
 *         thank you
 *         http://stackoverflow.com/questions/10530426/how-can-i-find-dominant-color-of-an-image
 */
public class ImageDominantColor {

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img
	 *            The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public static int[] getHexColor(BufferedImage image) {

		Map<Integer, Integer> colorMap = new HashMap<>();
		int height = image.getHeight();
		int width = image.getWidth();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = image.getRGB(i, j);
				if (!isGray(getRGBArr(rgb))) {
					Integer counter = colorMap.get(rgb);
					if (counter == null) {
						counter = 0;
					}

					colorMap.put(rgb, ++counter);
				}
			}
		}

		return getMostCommonColor(colorMap);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static int[] getMostCommonColor(Map<Integer, Integer> map) {
		List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());

		Collections.sort(list, (Map.Entry<Integer, Integer> obj1,
				Map.Entry<Integer, Integer> obj2) -> ((Comparable) obj1.getValue()).compareTo(obj2.getValue()));

		if (list.size() > 0) {
			Map.Entry<Integer, Integer> entry = list.get(list.size() - 1);
			int[] rgb = getRGBArr(entry.getKey());
			return rgb;
		} else {
			return new int[0];
		}
		// return "#" + Integer.toHexString(rgb[0]) +
		// Integer.toHexString(rgb[1]) + Integer.toHexString(rgb[2]);
	}

	@SuppressWarnings("unused")
	private static int[] getRGBArr(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;

		return new int[] { red, green, blue };
	}

	private static boolean isGray(int[] rgbArr) {
		int rgDiff = rgbArr[0] - rgbArr[1];
		int rbDiff = rgbArr[0] - rgbArr[2];
		// Filter out black, white and grays...... (tolerance within 10 pixels)
		int tolerance = 10;
		if (rgDiff > tolerance || rgDiff < -tolerance) {
			if (rbDiff > tolerance || rbDiff < -tolerance) {
				return false;
			}
		}
		return true;
	}
}