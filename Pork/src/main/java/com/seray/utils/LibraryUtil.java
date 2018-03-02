package com.seray.utils;

import android.content.Context;

import com.seray.entity.Library;
import com.seray.entity.LibraryList;
import com.seray.pork.dao.LibraryManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/12/15.
 */

public class LibraryUtil {
    private Context context;
    LibraryManager manager = LibraryManager.getInstance();
    private List<Library> library = new ArrayList<>();

    public LibraryUtil(Context context) {
        this.context = context;
    }

    public List<LibraryList> libraryJson(String key) {
        library = manager.queryAllLibrary();
        List<LibraryList> libraryList = new ArrayList<>();
        if (library.size() == 0) {
            return libraryList;
        }
        String data = library.get(0).getLibraryName();
        LogUtil.d("LibraryData",data);
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                if (!result.getString("key").equals(key))
                    continue;
                JSONArray come = result.getJSONArray("comealibrary");
                List<Library> list = new ArrayList<>();
                LibraryList libraryLi = new LibraryList();
                for (int j = 0; j < come.length(); j++) {
                    Library library = new Library();
                    JSONObject comeResult = come.getJSONObject(j);
                    String alibraryId = comeResult.getString("AlibraryId");
                    String alibraryName = comeResult.getString("AlibraryName");
                    int type= comeResult.getInt("Type");
                    library.setLibraryId(alibraryId);
                    library.setLibraryName(alibraryName);
                    library.setType(type);
                    list.add(library);
                }
                libraryLi.setLibraryList(list);

                JSONArray go = result.getJSONArray("goalibrary");
                List<Library> goList = new ArrayList<>();
                LibraryList libraryLiGo = new LibraryList();
                for (int h = 0; h < go.length(); h++) {
                    Library library = new Library();
                    JSONObject comeResult = go.getJSONObject(h);
                    String alibraryId = comeResult.getString("AlibraryId");
                    String alibraryName = comeResult.getString("AlibraryName");
                    library.setLibraryId(alibraryId);
                    library.setLibraryName(alibraryName);
                    goList.add(library);
                }
                libraryLiGo.setLibraryList(goList);
                libraryList.add(libraryLi);
                libraryList.add(libraryLiGo);
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return libraryList;
    }
}
