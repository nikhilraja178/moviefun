package org.superbiz.moviefun.blobstore;

import org.superbiz.moviefun.albums.AlbumsBean;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

//@Component
public class FileStore implements BlobStore {


    @Override
    public void put(Blob blob) throws IOException {
        String name = blob.name;
        InputStream inputStream = blob.inputStream;

        FileOutputStream outputStream = new FileOutputStream("covers/" + name);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        Blob blob = new Blob(name, new FileInputStream("covers/" + name), "image/jpeg");
        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

    }

    private final AlbumsBean albumsBean;

    public FileStore(AlbumsBean albumsBean) {
        this.albumsBean = albumsBean;
    }



    }

