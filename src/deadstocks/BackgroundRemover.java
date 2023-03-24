///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package deadstocks;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import javax.swing.JFileChooser;
//
//
//public class BackgroundRemover {
//    public static void main(String[] args) throws ClientProtocolException, IOException {
//        JFileChooser fileChooser = new JFileChooser();
//        int result = fileChooser.showOpenDialog(null);
//        if (result != JFileChooser.APPROVE_OPTION) {
//            System.exit(0);
//        }
//        File imageFile = fileChooser.getSelectedFile();
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost post = new HttpPost("https://sdk.photoroom.com/v1/segment");
//        post.setHeader(HttpHeaders.AUTHORIZATION, "abc123def456");
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.addBinaryBody("image_file", imageFile, ContentType.create("image/jpeg"), imageFile.getName());
//        HttpEntity entity = builder.build();
//        post.setEntity(entity);
//        HttpResponse response = httpClient.execute(post);
//        if (response.getStatusLine().getStatusCode() != 200) {
//            throw new IOException("Failed to remove background: " + response.getStatusLine().getReasonPhrase());
//        }
//        try (FileOutputStream fos = new FileOutputStream(new File(imageFile.getParentFile(), "result.jpg"))) {
//            response.getEntity().writeTo(fos);
//        }
//    }
//}
