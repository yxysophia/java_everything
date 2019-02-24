package com.github.core.search;

import com.github.core.model.Condition;
import com.github.core.model.Thing;

import java.util.List;

public interface  FileSearch {
    List<Thing> search(Condition condition);

}
