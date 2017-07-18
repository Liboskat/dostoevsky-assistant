package rizvanov.dostoevskyassistant.character_fragment;

/**
 * Created by user on 14.07.2017.
 */

public class Character {

    private String photo;
    private String name;
    private String editText;
    private String id;

    public Character(String photo, String name, String editText) {
        this.photo = photo;
        this.name = name;
        this.editText = editText;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEditText() {
        return editText;
    }

    public void setEditText(String editText) {
        this.editText = editText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
