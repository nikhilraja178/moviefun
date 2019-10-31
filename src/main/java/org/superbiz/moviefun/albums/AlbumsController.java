package org.superbiz.moviefun.albums;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {
    AlbumsBean albumsBean;
    BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        Blob blob = new Blob(String.valueOf(albumId), uploadedFile.getInputStream(), uploadedFile.getContentType());
        blobStore.put(blob);
        //saveUploadToFile(uploadedFile, getCoverFile(albumId));

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Optional<Blob> bloby = blobStore.get(String.valueOf(albumId));
        HttpHeaders headers = new HttpHeaders();
        byte[] imageBytes;
        if(bloby.isPresent()) {
            Blob blob = bloby.get();
            headers.setContentType(MediaType.parseMediaType(blob.contentType));
            imageBytes = getBytesFromInputStream(blob.inputStream);
        } else {
            ClassLoader classloader = null;
            InputStream is = classloader.getResourceAsStream("default-cover.jpg");
            imageBytes = getBytesFromInputStream(is);
        }
        headers.setContentLength(imageBytes.length);
        return new HttpEntity<>(imageBytes, headers);
    }

//
//    private void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {
//        targetFile.delete();
//        targetFile.getParentFile().mkdirs();
//        targetFile.createNewFile();
//
//        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
//            outputStream.write(uploadedFile.getBytes());
//        }
//    }
//
//    private HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes) throws IOException {
//        String contentType = new Tika().detect(coverFilePath);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType(contentType));
//        headers.setContentLength(imageBytes.length);
//        return headers;
//    }
//
//    private File getCoverFile(@PathVariable long albumId) {
//        String coverFileName = format("covers/%d", albumId);
//        return new File(coverFileName);
//    }
//
//    private Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
//        File coverFile = getCoverFile(albumId);
//        Path coverFilePath;
//
//        if (coverFile.exists()) {
//            coverFilePath = coverFile.toPath();
//        } else {
//            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
//        }
//
//        return coverFilePath;
//    }
    public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }
}
