package com.retail.management.controller;

import com.asprise.ocr.Ocr;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("qr")
public class QRCodeGenerator {
//    private final MultiLayerNetwork model;
    @PostMapping(value = "/generate/{batchId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQRCodeImage(@PathVariable String batchId)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(batchId, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
//    public QRCodeGenerator() throws Exception {
//        try (InputStream modelStream = getClass().getResourceAsStream("src/main/resources/archive.zip")) {
//            model = ModelSerializer.restoreMultiLayerNetwork(modelStream);
//        }
//    }
//    @PostMapping(value = "/read", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> readQRCode(@RequestParam("file") MultipartFile file) throws IOException, NotFoundException {
//        File tempFile = File.createTempFile("digit", ".png");
//        file.transferTo(tempFile);
//            BufferedImage img = ImageIO.read(tempFile);
//
//            // Convert to grayscale & resize to 28x28
//            BufferedImage grayImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
//            Graphics2D g = grayImg.createGraphics();
//            g.drawImage(img, 0, 0, 28, 28, null);
//            g.dispose();
//
//            // Normalize pixel values
//            INDArray input = Nd4j.create(1, 28 * 28);
//            int index = 0;
//            for (int y = 0; y < 28; y++) {
//                for (int x = 0; x < 28; x++) {
//                    int rgb = grayImg.getRGB(x, y) & 0xFF;
//                    double norm = (255 - rgb) / 255.0; // invert colors
//                    input.putScalar(index++, norm);
//                }
//            }
//
//            // Predict
//            INDArray output = model.output(input);
//            Integer digit= Nd4j.argMax(output, 1).getInt(0);
//        tempFile.delete();
//
//        return ResponseEntity.ok("Detected Digit: " + digit);
//    }
//    @PostMapping(value = "/read", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public static String readQRCode(@RequestParam("file") MultipartFile file) throws IOException, NotFoundException {
//        File tempFile = File.createTempFile("ocr_upload_", ".png");
//        file.transferTo(tempFile);
//
//        // Run OCR
//        Ocr.setUp();
//        Ocr ocr = new Ocr();
//        ocr.startEngine("eng", Ocr.SPEED_FASTEST);
//        String text = ocr.recognize(new File[]{tempFile},
//                Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_PLAINTEXT);
//        ocr.stopEngine();
//
//        // Delete temp file after use
//        tempFile.delete();
//        System.out.println("read number as:: " + text);
//        return text.trim();
//    }
    @PostMapping(value = "/read", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public static String readQRCode(@RequestParam("file") MultipartFile file) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}
