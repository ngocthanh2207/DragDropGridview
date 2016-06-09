package com.example.androidimageviewlist;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	//items stored in ListView
	public class Item {
		Drawable ItemDrawable;
		String ItemString;
		Item(Drawable drawable, String t){
			ItemDrawable = drawable;
			ItemString = t;
		}
	}
	
	//objects passed in Drag and Drop operation
	class PassObject{
		View view;
		Item item;
		List<Item> srcList;
		
		PassObject(View v, Item i, List<Item> s){
			view = v;
			item = i;
			srcList = s;
		}
	}
	
	static class ViewHolder {
		ImageView icon;
		TextView text;	
	}
	
	static class GridViewHolder {
		ImageView icon;	
	}
	
	public class ItemBaseAdapter extends BaseAdapter {

		Context context;
		List<Item> list;

		ItemBaseAdapter(Context c, List<Item> l){
			context = c;
			list = l;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public List<Item> getList(){
			return list;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

	public class ItemListAdapter extends ItemBaseAdapter {
		
		ItemListAdapter(Context c, List<Item> l) {
			super(c, l);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			
		    // reuse views
		    if (rowView == null) {
		    	LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		    	rowView = inflater.inflate(R.layout.row, null);

		    	ViewHolder viewHolder = new ViewHolder();
		    	viewHolder.icon = (ImageView) rowView.findViewById(R.id.rowImageView);
		    	viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
		    	rowView.setTag(viewHolder);	
		    }

		    ViewHolder holder = (ViewHolder) rowView.getTag();
		    holder.icon.setImageDrawable(list.get(position).ItemDrawable);
		    holder.text.setText(list.get(position).ItemString);
		    
		    rowView.setOnDragListener(new ItemOnDragListener(list.get(position)));

		    return rowView;
		}

	}
	
	public class ItemGridAdapter extends ItemBaseAdapter {

		ItemGridAdapter(Context c, List<Item> l) {
			super(c, l);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View gridrowView = convertView;
			
		    // reuse views
		    if (gridrowView == null) {
		    	LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		    	gridrowView = inflater.inflate(R.layout.gridrow, null);

		    	GridViewHolder gridviewHolder = new GridViewHolder();
		    	gridviewHolder.icon = (ImageView) gridrowView.findViewById(R.id.gridrowImageView);
		    	gridrowView.setTag(gridviewHolder);	
		    }

		    GridViewHolder holder = (GridViewHolder) gridrowView.getTag();
		    holder.icon.setImageDrawable(list.get(position).ItemDrawable);

		    gridrowView.setOnDragListener(new ItemOnDragListener(list.get(position)));

		    return gridrowView;
		}
		
	}

	List<Item> items1, items2, items3;
	ListView listView1, listView2;
	GridView gridView3;
	ItemListAdapter myItemListAdapter1, myItemListAdapter2;
	ItemGridAdapter myItemGridAdapter3;
	LinearLayoutAbsListView area1, area2, area3;
	TextView prompt;
	
	//Used to resume original color in drop ended/exited
	int resumeColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView1 = (ListView)findViewById(R.id.listview1);
		listView2 = (ListView)findViewById(R.id.listview2);
		gridView3 = (GridView)findViewById(R.id.gridview3);
		
		area1 = (LinearLayoutAbsListView)findViewById(R.id.pane1);
		area2 = (LinearLayoutAbsListView)findViewById(R.id.pane2);
		area3 = (LinearLayoutAbsListView)findViewById(R.id.pane3);
		area1.setOnDragListener(myOnDragListener);
		area2.setOnDragListener(myOnDragListener);
		area3.setOnDragListener(myOnDragListener);
		area1.setAbsListView(listView1);
		area2.setAbsListView(listView2);
		area3.setAbsListView(gridView3);
		
		initItems();
		myItemListAdapter1 = new ItemListAdapter(this, items1);
		myItemListAdapter2 = new ItemListAdapter(this, items2);
		myItemGridAdapter3 = new ItemGridAdapter(this, items3);
		listView1.setAdapter(myItemListAdapter1);
		listView2.setAdapter(myItemListAdapter2);
		gridView3.setAdapter(myItemGridAdapter3);
		
		listView1.setOnItemClickListener(listOnItemClickListener);
		listView2.setOnItemClickListener(listOnItemClickListener);
		gridView3.setOnItemClickListener(listOnItemClickListener);
		
		listView1.setOnItemLongClickListener(myOnItemLongClickListener);
		listView2.setOnItemLongClickListener(myOnItemLongClickListener);
		gridView3.setOnItemLongClickListener(myOnItemLongClickListener);
		
		prompt = (TextView) findViewById(R.id.prompt);
		// make TextView scrollable
		prompt.setMovementMethod(new ScrollingMovementMethod());
		//clear prompt area if LongClick
		prompt.setOnLongClickListener(new OnLongClickListener(){
			
			@Override
			public boolean onLongClick(View v) {
				prompt.setText("");
				return true;	
			}});
		
		resumeColor  = getResources().getColor(android.R.color.background_light);

	}
	
	OnItemLongClickListener myOnItemLongClickListener = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Item selectedItem = (Item)(parent.getItemAtPosition(position));
			
			ItemBaseAdapter associatedAdapter = (ItemBaseAdapter)(parent.getAdapter());
		    List<Item> associatedList = associatedAdapter.getList();
			
			PassObject passObj = new PassObject(view, selectedItem, associatedList);

			ClipData data = ClipData.newPlainText("", "");
			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
			view.startDrag(data, shadowBuilder, passObj, 0);
			
			return true;
		}
		
	};
	
	OnDragListener myOnDragListener = new OnDragListener() {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			String area;
			if(v == area1){
				area = "area1";	
			}else if(v == area2){
				area = "area2";	
			}else if(v == area3){
				area = "area3";	
			}else{
				area = "unknown";	
			}
			
			switch (event.getAction()) {
				case DragEvent.ACTION_DRAG_STARTED:
					prompt.append("ACTION_DRAG_STARTED: " + area  + "\n");
					break;	
				case DragEvent.ACTION_DRAG_ENTERED:
					prompt.append("ACTION_DRAG_ENTERED: " + area  + "\n");
					break;	
				case DragEvent.ACTION_DRAG_EXITED:
					prompt.append("ACTION_DRAG_EXITED: " + area  + "\n");
					break;	
				case DragEvent.ACTION_DROP:
					prompt.append("ACTION_DROP: " + area  + "\n");

					PassObject passObj = (PassObject)event.getLocalState();
					View view = passObj.view;
					Item passedItem = passObj.item;
					List<Item> srcList = passObj.srcList;
					AbsListView oldParent = (AbsListView)view.getParent();
					ItemBaseAdapter srcAdapter = (ItemBaseAdapter)(oldParent.getAdapter());
					
					LinearLayoutAbsListView newParent = (LinearLayoutAbsListView)v;
					ItemBaseAdapter destAdapter = (ItemBaseAdapter)(newParent.absListView.getAdapter());
				    List<Item> destList = destAdapter.getList();
					
					if(removeItemToList(srcList, passedItem)){
						addItemToList(destList, passedItem);
					}
					
					srcAdapter.notifyDataSetChanged();
					destAdapter.notifyDataSetChanged();
					
					//smooth scroll to bottom
					newParent.absListView.smoothScrollToPosition(destAdapter.getCount()-1);
					
					break;
			   case DragEvent.ACTION_DRAG_ENDED:
				   prompt.append("ACTION_DRAG_ENDED: " + area  + "\n");  
			   default:
				   break;	   
			}
			   
			return true;
		}
		
	};
	
	class ItemOnDragListener implements OnDragListener{
		
		Item  me;
		
		ItemOnDragListener(Item i){
			me = i;
		}

		@Override
		public boolean onDrag(View v, DragEvent event) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				prompt.append("Item ACTION_DRAG_STARTED: " + "\n");
				break;	
			case DragEvent.ACTION_DRAG_ENTERED:
				prompt.append("Item ACTION_DRAG_ENTERED: " + "\n");
				v.setBackgroundColor(0x30000000);
				break;	
			case DragEvent.ACTION_DRAG_EXITED:
				prompt.append("Item ACTION_DRAG_EXITED: " + "\n");
				v.setBackgroundColor(resumeColor);
				break;	
			case DragEvent.ACTION_DROP:
				prompt.append("Item ACTION_DROP: " + "\n");

				PassObject passObj = (PassObject)event.getLocalState();
				View view = passObj.view;
				Item passedItem = passObj.item;
				List<Item> srcList = passObj.srcList;
				AbsListView oldParent = (AbsListView)view.getParent();
				ItemBaseAdapter srcAdapter = (ItemBaseAdapter)(oldParent.getAdapter());
				
				AbsListView newParent = (AbsListView)v.getParent();
				ItemBaseAdapter destAdapter = (ItemBaseAdapter)(newParent.getAdapter());
				List<Item> destList = destAdapter.getList();
				
				int removeLocation = srcList.indexOf(passedItem);
				int insertLocation = destList.indexOf(me);
				/*
				 * If drag and drop on the same list, same position,
				 * ignore
				 */
				if(srcList != destList || removeLocation != insertLocation){
					if(removeItemToList(srcList, passedItem)){
						destList.add(insertLocation, passedItem);
					}
					
					srcAdapter.notifyDataSetChanged();
					destAdapter.notifyDataSetChanged();
				}

				v.setBackgroundColor(resumeColor);
				
				break;
		   case DragEvent.ACTION_DRAG_ENDED:
			   prompt.append("Item ACTION_DRAG_ENDED: "  + "\n");
			   v.setBackgroundColor(resumeColor);
		   default:
			   break;	   
		}
		   
		return true;
		}
		
	}
	
	OnItemClickListener listOnItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(MainActivity.this, 
					((Item)(parent.getItemAtPosition(position))).ItemString, 
					Toast.LENGTH_SHORT).show();
		}
		
	};

	private void initItems(){
		items1 = new ArrayList<Item>();
		items2 = new ArrayList<Item>();
		items3 = new ArrayList<Item>();
		
		TypedArray arrayDrawable = getResources().obtainTypedArray(R.array.resicon);
		TypedArray arrayText = getResources().obtainTypedArray(R.array.restext);
		
		for(int i=0; i<arrayDrawable.length(); i++){
			Drawable d = arrayDrawable.getDrawable(i);
			String s = arrayText.getString(i);
			Item item = new Item(d, s);
			items1.add(item);
		}
		
		arrayDrawable.recycle();
		arrayText.recycle();
	}
	
	private boolean removeItemToList(List<Item> l, Item it){
		boolean result = l.remove(it);
		return result;
	}
	
	private boolean addItemToList(List<Item> l, Item it){
		boolean result = l.add(it);
		return result;
	}

}
