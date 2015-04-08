package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

// LoLBuildData.java

// Contains the info necessary to display the build of a player.

public class LoLBuildData implements Serializable {
    private transient List<Bitmap> items;

    public LoLBuildData(){
        this.items = new LinkedList<>();
    }

    public void addItem(Bitmap m){
        this.items.add(m);
    }

    public Bitmap getItem(int pos){
        return this.items.get(pos);
    }

    public int getCount(){
        return this.items.size();
    }


    // Necessary to change the way that Android writes our object (remember this object implements
    // the interface Serializable) to send it.
    private void writeObject(ObjectOutputStream oos) throws IOException {
        // Write all non-transient;
        oos.defaultWriteObject();
        // Write the images.
        for (int i = 0; i < this.items.size(); i++)
            if (this.items.get(i) != null) {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                boolean success = this.items.get(i).compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                if (success) {
                    oos.writeObject(byteStream.toByteArray());
                }
            }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Deserializing now - in the SAME ORDER!
        ois.defaultReadObject();
        for (int i = 0; i < this.items.size(); i++) {
            byte[] image = (byte[]) ois.readObject();
            if (image != null && image.length > 0) {
                this.items.add(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
        }
    }
}
