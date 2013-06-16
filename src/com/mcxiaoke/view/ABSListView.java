package com.mcxiaoke.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * The ABSListView is a compatibility class, to getIcon the ListView working together
 * with the ActionbarSherlock compatibility package.
 * It offers support for multiple selection with the context action bar of ActionBarSherlock.
 * The batch contextual actions in ListView (http://developer.android.com/guide/topics/ui/menus.html#CABforListView)
 * will be enabled with the use of this class, but have a slightly different syntax.
 * <p/>
 * You must use ListView.CHOICE_MODE_MULTIPLE as choice mode (not CHOICE_MODE_MULTIPLE_MODAL),
 * and use the ABSListView.MultiChoiceModeListener interface, instead of the native
 * android interface. Everything else should nearly work like in the native android
 * implementation.
 * <p/>
 * The ABSListView must be used inside of a SherlockActivity, otherwise it will
 * throw an error.
 * <p/>
 * TODO: We expect the items of the listview to be Checkable. That makes in our case
 * (and most cases I can think of) sense. If the ListView items do not implement
 * Checkable, they will not anyhow be highlighted, by selecting them.
 * Anyhow it is valid for the items not implement Checkable. For that cases
 * we must save  the checked state in an own list, and not casting the views
 * to Checkable without further tests.
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class ABSListView extends ListView {

    private ActionMode actionmode;
    private MultiChoiceModeListenerWrapper multiChoiceListener;
    private OnItemLongClickListenerWrapper longClickWrapper = new OnItemLongClickListenerWrapper();
    private OnItemClickListenerWrapper clickWrapper = new OnItemClickListenerWrapper();

    private SherlockActivity sherlockActivity;
    private SherlockFragmentActivity sherlockFragmentActivity;

    /**
     * Get the SherlockActivity, the ListView is used in.
     *
     * @return The parent SherlockActivity.
     */
    private ActionMode startActionMode(MultiChoiceModeListener listener) {

        Context ctx = getContext();

        if (SherlockActivity.class.isAssignableFrom(ctx.getClass())) {
            return ((SherlockActivity) ctx).startActionMode(multiChoiceListener);
        } else if (SherlockFragmentActivity.class.isAssignableFrom(ctx.getClass())) {
            return ((SherlockFragmentActivity) ctx).startActionMode(multiChoiceListener);
        } else {
            throw new Error("The view is not inside a SherlockActivity.");
        }

    }

    public ABSListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ABSListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ABSListView(Context context) {
        super(context);
        init();
    }

    /**
     * Initialize the ListView. This will register the click and long click
     * wrapper as listener at the super class. So they can handle the click
     * and long click events, and eventually forward them to another register.
     */
    private void init() {
        super.setOnItemLongClickListener(longClickWrapper);
        super.setOnItemClickListener(clickWrapper);
    }

    /**
     * The MultiChoiceModeListener must be implemented, to handle the selection
     * of multiple elements in this ListView.
     */
    public interface MultiChoiceModeListener extends ActionMode.Callback {

        /**
         * Called when an item is checked or unchecked during selection mode.
         *
         * @param mode     The ActionMode providing the selection mode.
         * @param position Adapter position of the item that was checked or unchecked.
         * @param id       Adapter ID of the item that was checked or unchecked.
         * @param checked  Whether the item is now checked.
         */
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked);

    }

    /**
     * Register a callback to be invoked when an item in the list has been clicked.
     * The listener won't be notified when the list is in the multiple selection
     * mode.
     *
     * @param listener The listener to be notified.
     */
    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        clickWrapper.listener = listener;
    }

    /**
     * Register a callback to be invoked when an item in the list has been long
     * clicked. The listener won't be notified when the list is in the multiple
     * selection mode.
     *
     * @param listener The listener to be notified.
     */
    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickWrapper.listener = listener;
    }

    /**
     * Returns the number of items currently selected. This will only be valid
     * if the choice mode is not CHOICE_MODE_NONE (default).
     *
     * @return The number of items currently selected.
     */
    @Override
    public int getCheckedItemCount() {

        int checked = 0;

        for (int i = 0; i < getCheckedItemPositions().size(); i++) {
            if (getCheckedItemPositions().valueAt(i)) {
                checked++;
            }
        }

        return checked;

    }

    /**
     * DO NOT USE THIS METHOD. Use the setMultiChoiceModeListener(ABSListView.MultiChoiceModeListener)
     * method instead. It is replacing this method for compatibility reasons.
     *
     * @param listener DO NOT USE
     */
    @Override
    public final void setMultiChoiceModeListener(android.widget.AbsListView.MultiChoiceModeListener listener) {
        throw new UnsupportedOperationException("Use the alternative implementation.");
    }

    /**
     * Set a MultiChoiceModeListener, that will manage the lifecycle of the selection
     * ActionMode. This will be only used, when the choice mode is set to
     * CHOICE_MODE_MULTIPLE.
     *
     * @param listener Listener that will manage the lifecycle.
     */
    public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
        MultiChoiceModeListenerWrapper wrapper = new MultiChoiceModeListenerWrapper(listener);
        this.multiChoiceListener = wrapper;
        // Don't call the super function for this, since it doesn't exist before API 11
        // We will try to imitate its behaviour.
    }

    /**
     * This wrapper handles the item clicks and will eventually forward them
     * to a registered OnItemClickListener.
     */
    private class OnItemClickListenerWrapper implements OnItemClickListener {

        private OnItemClickListener listener;

        /**
         * The method will be called, if an item has been clicked.
         *
         * @param parent   The parent view of the clicked item.
         * @param view     The view, that has been clicked.
         * @param position The position in the list, that has been clicked.
         * @param id       The id of the clicked item.
         */
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // If an actionmode has been set, just mark the item and inform the
            // listener about a changed item state.
            if (actionmode != null) {
                ABSListView.this.setItemChecked(position, !((Checkable) view).isChecked());
                multiChoiceListener.onItemCheckedStateChanged(actionmode, position, id, ((Checkable) view).isChecked());
                return;
            }

            // If a OnItemClickListener has been registered notify it.
            if (listener != null)
                listener.onItemClick(parent, view, position, id);
        }

    }

    /**
     * This wrapper handles the long clicks on items and will eventuellay forward
     * them to a registered OnItemLongClickListener.
     */
    private class OnItemLongClickListenerWrapper implements OnItemLongClickListener {

        private OnItemLongClickListener listener;

        /**
         * This method will be called when an item has been long clicked.
         *
         * @param parent   The parent view of the clicked item.
         * @param view     The long clicked item.
         * @param position The position in the list, that has been clicked.
         * @param id       The id of the clicked item.
         * @return Whether the click has been handled by the wrapper.
         */
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            // If choice mode is none, just notify the original listener.
            // TODO: CHOICE_MODE_SINGLE, how is it handled in android native?
            if (getChoiceMode() == CHOICE_MODE_NONE || getChoiceMode() == CHOICE_MODE_SINGLE) {
                if (listener != null) {
                    return listener.onItemLongClick(parent, view, position, id);
                } else {
                    return false;
                }
            } else if (getChoiceMode() == CHOICE_MODE_MULTIPLE && multiChoiceListener != null) {

                // Start actionmode on the parent SherlockActivity
                if (actionmode == null) {
                    actionmode = ABSListView.this.startActionMode(multiChoiceListener);
                }

                // Check the item and inform the MultiChoiceModeListener
                ABSListView.this.setItemChecked(position, !((Checkable) view).isChecked());
                multiChoiceListener.onItemCheckedStateChanged(actionmode, position, id, ((Checkable) view).isChecked());

                return true;

            }

            return false;
        }

    }

    /**
     * This wrapper handles the multiple selection mode calls.
     */
    private class MultiChoiceModeListenerWrapper implements MultiChoiceModeListener {

        private MultiChoiceModeListener listener;

        /**
         * Create a new wrapper with a MultiChoiceModeListener.
         *
         * @param listener The listener to use.
         */
        public MultiChoiceModeListenerWrapper(MultiChoiceModeListener listener) {
            this.listener = listener;
        }

        /**
         * This method will be called whenever an item has been checked or unchecked.
         * This will inform the listener and finish the ActionMode, if the last
         * item has been unchecked.
         *
         * @param mode     The ActionMode providing the selection mode.
         * @param position Adapter position of the item that was checked or unchecked.
         * @param id       Adapter ID of the item that was checked or unchecked.
         * @param checked  Whether the item is now checked.
         */
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            listener.onItemCheckedStateChanged(mode, position, id, checked);
            if (ABSListView.this.getCheckedItemCount() == 0) {
                mode.finish();
            }
        }

        /**
         * This method will be called when the action mode is about to be created.
         * This will just forward to the listener, that should fill the menu.
         *
         * @param mode The ActionMode created.
         * @param menu The menu of the ActionMode.
         * @return Whether the menu has been changed.
         */
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return listener.onCreateActionMode(mode, menu);
        }

        /**
         * Called to refresh an action mode's action menu whenever it is
         * invalidated.
         *
         * @param mode The current ActionMode.
         * @param menu The menu to be refreshed.
         * @return Whether the menu has been changed.
         */
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return listener.onPrepareActionMode(mode, menu);
        }

        /**
         * Called to report a user click on an action button.
         *
         * @param mode The current ActionMode.
         * @param item The item that was clicked.
         * @return Whether the callback handled the event.
         */
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return listener.onActionItemClicked(mode, item);
        }

        /**
         * Called when the ActionMode is about to be exited and destroyed.
         *
         * @param mode The current ActionMode being destroyed.
         */
        public void onDestroyActionMode(ActionMode mode) {
            listener.onDestroyActionMode(mode);
            actionmode = null;
            ABSListView.this.clearChoices();
            ABSListView.this.requestLayout();
        }

    }

}
