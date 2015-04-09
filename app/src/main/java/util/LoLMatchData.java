package util;

// LoLMatchData.java

// Contains the info necessary to display a "MatchDataActivity.java"

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LoLMatchData implements Serializable {
    private transient JSONObject jsonObject;
    private transient Bitmap[] champImages;

    public LoLMatchData(JSONObject jsonObject, Bitmap[] champImages) {
        this.jsonObject = jsonObject;
        this.champImages = champImages;
    }

    public JSONObject getJsonObject() {
        return this.jsonObject;
    }

    public Bitmap[] getChampImages() {
        return this.champImages;
    }

    // Necessary to change the way that Android writes our object (remember this object implements
    // the interface Serializable) to send it.
    private void writeObject(ObjectOutputStream oos) throws IOException {
        // Write all non-transient;
        oos.defaultWriteObject();
        // Manually serialize all transient fields that you want to be serialized.
        // 1st: write the JSONObject (match data)
        oos.writeObject(this.jsonObject.toString());
        // 2nd: write the images.
        for (Bitmap champImage : this.champImages)
            if (champImage != null) {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                boolean success = champImage.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                if (success) {
                    oos.writeObject(byteStream.toByteArray());
                }
            }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Deserializing now - in the SAME ORDER!
        ois.defaultReadObject();
        // 1st.
        try {
            this.jsonObject = new JSONObject((String) ois.readObject());
        } catch (JSONException e) {
            this.jsonObject = null;
        }
        // 2nd.
        for (int i = 0; i < this.champImages.length; i++) {
            byte[] image = (byte[]) ois.readObject();
            if (image != null && image.length > 0) {
                this.champImages[i] = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
        }
    }
}
