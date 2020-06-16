
package com.perseverance.phando.genericAdopter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * Abstract diff util calback {@link DiffUtil.Callback}
 * which simplifies concrete implementation by doing some basics.
 * Just extend it with specifying a Type and implement remaining abstract methods,
 * such as {@link DiffUtil.Callback#areItemsTheSame(int, int)} and
 * {@link DiffUtil.Callback#areContentsTheSame(int, int)}
 *
 * @param <T> type of objects used in the adapter's datasetzx
 * @since 1.0.0
 */

public abstract class BaseDiffCallback<T> extends DiffUtil.Callback {

    private List<T> oldItems;
    private List<T> newItems;

    public BaseDiffCallback(List<T> oldItems, List<T> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

    public List<T> getOldItems() {
        return oldItems;
    }

    public List<T> getNewItems() {
        return newItems;
    }
}
