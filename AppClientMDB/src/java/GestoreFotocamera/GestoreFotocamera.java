package GestoreFotocamera;

import Exceptions.ConnectionErrorException;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JFrame;

public class GestoreFotocamera extends JFrame {

    private static final long serialVersionUID = 6441489157408381878L;
    private static Webcam webcam = null;
    private static WebcamPanel panel = null;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    /* Metodo che apre lo stream della webcam e restituisce il codice isbn acquisito */
    public static String acquisisciIsbn() throws InterruptedException, ExecutionException {
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setTitle("MDB Barcode Scanner");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension size = WebcamResolution.QVGA.getSize();
        webcam = Webcam.getWebcams().get(0);
        webcam.setViewSize(size);

        panel = new WebcamPanel(webcam);
        panel.setPreferredSize(size);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        Future<String> future = executor.submit(callable);
        String isbn = future.get();

        if (isbn.length() > 1) {

            //executor.shutdown();
            future.cancel(true);
            panel.setEnabled(false);
            panel.stop();
            webcam.close();
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }

        return isbn;
    }

    /* Metodo privato che estrae il codice ISBN analizzando l'immagine */
    private static String estraiISBNDaImmagine(BufferedImage image) {
        Result barcode = null;
        Collection<BarcodeFormat> c = new ArrayList<>();
        c.add(BarcodeFormat.EAN_13);

        HashMap<DecodeHintType, Object> decodeHints = new HashMap<>();
        decodeHints.put(DecodeHintType.POSSIBLE_FORMATS, c);
        decodeHints.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");
        decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        //decodeHints.put(DecodeHintType.ALLOWED_LENGTHS, 13);

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            barcode = new MultiFormatReader().decode(bitmap, decodeHints);
        } catch (NotFoundException ex) {
            //Logger.getLogger(GestoreFotocamera.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (barcode != null) {
            return barcode.getText();
        } else {
            return "";
        }
    }

    /* Thread che acquisisce immagine dallo stream della webcam */
    private static final Callable<String> callable = new Callable<String>() {
        @Override
        public String call() throws MalformedURLException, ConnectionErrorException {
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BufferedImage image = null;
                if (webcam.isOpen()) {
                    if ((image = webcam.getImage()) == null) {
                        continue;
                    }
                }
                String isbn = estraiISBNDaImmagine(image);
                if (!isbn.equals("")) {
                    return isbn;
                }
            } while (true);
        }

    };

}
