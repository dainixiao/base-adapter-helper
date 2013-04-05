package com.joanzap.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class BaseAdapterHelper {
    private View convertView;
    private int layoutId;
    private final Context context;
    private final List<IdValue> actions;

    public static BaseAdapterHelper get(Context context) {
        return new BaseAdapterHelper(context);
    }

    private BaseAdapterHelper(Context context) {
        actions = new ArrayList<BaseAdapterHelper.IdValue>();
        this.context = context;
    }

    public BaseAdapterHelper convertView(View convertView) {
        this.convertView = convertView;
        return this;
    }

    public BaseAdapterHelper layout(int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public BaseAdapterHelper setText(int viewId, String value) {
        actions.add(new IdValue(viewId, value, Action.SET_TEXT));
        return this;
    }

    public View build() {
        ViewHolder holder;
        if (convertView == null) {
            // If view doesn't exist, create the view
            // then create the view holder, put it into
            // the view and execute all the actions
            convertView = LayoutInflater//
                    .from(context) //
                    .inflate(layoutId, null);

            holder = new ViewHolder(actions.size());
            for (IdValue key : actions) {
                holder.addKeyAndApply(key.viewId, //
                        convertView.findViewById(key.viewId), //
                        key.action, key.value);
            }
            convertView.setTag(holder);

        } else {

            // If the view exists already, get the tag
            // and apply the values
            holder = (ViewHolder) convertView.getTag();
            for (IdValue key : actions) {
                holder.applyValue(key.viewId, key.value);
            }
        }

        return convertView;

    }

    private static class ViewHolder {
        private final SparseArray<ViewAction> actions;

        public ViewHolder(int nbActions) {
            actions = new SparseArray<ViewAction>(nbActions);
        }

        public void addKeyAndApply(int viewId, View view, Action action, Object value) {
            ViewAction viewAction = addKey(viewId, view, action);
            viewAction.applyValue(value);
        }

        public ViewAction addKey(int key, View view, Action action) {
            ViewAction viewAction = new ViewAction(view, action);
            actions.put(key, viewAction);
            return viewAction;
        }

        public void applyValue(Integer key, Object value) {
            actions.get(key).applyValue(value);
        }
    }

    private enum Action {
        SET_TEXT
    }

    private static class ViewAction {
        public View view;

        public Action action;

        public ViewAction(View view, Action action) {
            this.view = view;
            this.action = action;
        }

        public void applyValue(Object value) {
            switch (action) {
            case SET_TEXT:
                ((TextView) view).setText((String) value);
                break;
            }
        }

    }

    private static class IdValue {
        final int viewId;
        final Object value;
        final Action action;

        public IdValue(int viewId, Object value, Action action) {
            super();
            this.viewId = viewId;
            this.value = value;
            this.action = action;
        }

    }
}
