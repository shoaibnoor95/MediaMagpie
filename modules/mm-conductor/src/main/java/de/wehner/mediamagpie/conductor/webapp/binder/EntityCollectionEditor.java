package de.wehner.mediamagpie.conductor.webapp.binder;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import de.wehner.mediamagpie.persistence.Dao;
import de.wehner.mediamagpie.persistence.entity.Base;


public class EntityCollectionEditor<T extends Base> extends PropertyEditorSupport {

    private final Dao<T> _dao;

    public EntityCollectionEditor(Dao<T> dao) {
        _dao = dao;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String[] splits = text.indexOf(",") > -1 ? text.split(",") : new String[] { text };
        List<T> list = new ArrayList<T>();
        for (String split : splits) {
            T t = _dao.getById(Long.parseLong(split));
            list.add(t);
        }
        setValue(list);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getAsText() {
        List<T> list = (List<T>) getValue();
        list = list == null ? new ArrayList<T>() : list;
        String text = "";
        for (T t : list) {
            if (text.length() > 0) {
                text += ",";
            }
            text += t.getId();
        }
        return text;
    }

}
