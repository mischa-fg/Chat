package com.ubs.backend.classes;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Class to get the URI of a BLOB
 *
 * @author Marc
 * @since 17.07.2021
 */
public class URI {
    /**
     * Function to get the URI from a BLOB
     *
     * @param blob        the BLOB which is being converted
     * @param contentType the MimeType of the BLOB
     * @return the Base64 URI of the BLOB
     * @throws IOException  if there is an error in the BLOB
     * @throws SQLException if the BLOB couldn't be converted
     * @author Marc
     * @since 17.07.2021
     */
    public static String getURI(Blob blob, String contentType) throws IOException, SQLException {

        byte[] data = blob.getBytes(1, (int) blob.length());

        String base64 = DatatypeConverter.printBase64Binary(data);

        return "data:" + contentType + ";base64," + base64;
    }
}
