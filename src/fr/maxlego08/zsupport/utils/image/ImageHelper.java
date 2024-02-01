package fr.maxlego08.zsupport.utils.image;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

public class ImageHelper {

	public static int[] getHexColor(String url) throws IOException{
		URL urlImage = new URL(url);
		HttpsURLConnection conImage = (HttpsURLConnection) urlImage.openConnection();

		conImage.setRequestMethod("GET");
		conImage.setRequestProperty("User-Agent", "Mozilla/5.0");
		conImage.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		Image image = ImageIO.read(conImage.getInputStream());
		return ImageDominantColor.getHexColor(ImageDominantColor.toBufferedImage(image));
	}
	
}
