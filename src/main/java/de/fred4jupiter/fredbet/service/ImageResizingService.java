package de.fred4jupiter.fredbet.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ImageResizingService {

	// TODO: make configurable
	private static final int THUMBNAIL_SIZE = 40;

	private static final int DEFAULT_MAX_SIZE = 1920;

	private static final Logger log = LoggerFactory.getLogger(ImageResizingService.class);

	public byte[] createThumbnail(byte[] imageBinary) {
		return minimizeToSize(imageBinary, THUMBNAIL_SIZE);
	}

	public byte[] minimizeToDefaultSize(byte[] imageBinary) {
		return minimizeToSize(imageBinary, DEFAULT_MAX_SIZE);
	}

	public byte[] minimizeToSize(byte[] imageBinary, int size) {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(imageBinary);
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {

			BufferedImage bufferedImage = ImageIO.read(byteIn);
			if (bufferedImage.getWidth() <= size && bufferedImage.getHeight() <= size) {
				log.info(
						"The original image does have the prefered size of {} or is smaller than that. Do not change image size. image with: {}, image height: {}",
						size, bufferedImage.getWidth(), bufferedImage.getHeight());
				return imageBinary;
			}

			Thumbnails.of(bufferedImage).size(size, size).outputFormat("JPEG").toOutputStream(byteOut);
			return byteOut.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
}
