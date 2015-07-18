package com.avinab.trusssolver.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.R;
import com.avinab.trusssolver.data.Load;
import com.avinab.trusssolver.data.Member;
import com.avinab.trusssolver.data.Node;
import com.avinab.trusssolver.data.Truss;
import com.avinab.trusssolver.data.TrussIO;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.widgets.TrussItemSelectListener;
import com.avinab.trusssolver.widgets.TrussView;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
	public static UIState uiState = UIState.NONE;
	public TrussView trussView;
	protected ActionMode mActionMode;
	int MemberNodeID = -1;//-1 when no node is selected
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback()
	{

		@Override
		public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
		{
			MenuInflater inflater = actionMode.getMenuInflater();
			inflater.inflate(R.menu.contextual, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
		{
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
		{
			switch (menuItem.getItemId())
			{
				case R.id.action_context_delete:
					Global.CurrentTruss.DeleteSelected();
					actionMode.finish();
					return true;
				case R.id.action_context_edit:
					Object sel = Global.CurrentTruss.getFirstSelectedItem();
					if (sel instanceof Node)
					{
						actionMode.finish();
						ShowEditNodeDialog((Node) sel);
						return true;
					} else if (sel instanceof Load)
					{
						actionMode.finish();
						AddLoadDialog dlg = new AddLoadDialog();
						dlg.setPointLoad((Load) sel);
						dlg.show(MainActivity.this.getFragmentManager(), "add_load");
						setUiState(UIState.NONE);
					} else if (sel instanceof Member)
					{
						actionMode.finish();
						showEditMemberDialog((Member) sel);
						return true;
					}
					return false;
				default:
					return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode)
		{
			Global.CurrentTruss.UnselectAll();
			trussView.infoBox.isVisible = false;
		}
	};
	public TrussItemSelectListener trussItemSelectListener = new TrussItemSelectListener()
	{
		@Override
		public boolean onNodePressed(Node n)
		{

			if (uiState == UIState.ADD_MEMBER)
			{
				n.Select();
				selectNodeForElement(n);
			} else if (uiState == UIState.ADD_HINGE_SUPPORT)
			{
				Global.CurrentTruss.AddHingeSupport(n);
				setUiState(UIState.NONE);

			} else if (uiState == UIState.ADD_ROLLER_SUPPORT)
			{
				Global.CurrentTruss.AddRollerSupport(n, false);
				setUiState(UIState.NONE);

			} else if (uiState == UIState.ADD_HROLLER_SUPPORT)
			{
				Global.CurrentTruss.AddRollerSupport(n, true);
				setUiState(UIState.NONE);

			} else if (uiState == UIState.DELETE_SUPPORT)
			{
				Global.CurrentTruss.RemoveSupportFromNode(n);
				Global.CurrentTruss.Solved = false;
				Global.CurrentTruss.Modified = true;
				Global.CurrentTruss.UnselectAll();
				setUiState(UIState.NONE);

			} else if (uiState == UIState.ADD_POINT_LOAD)
			{
				AddLoadDialog dlg = new AddLoadDialog();
				dlg.setNode(n);
				dlg.show(MainActivity.this.getFragmentManager(), "add_load");

				setUiState(UIState.NONE);
			} else
			{
				if (mActionMode != null) mActionMode.finish();
				Global.CurrentTruss.UnselectAll();
				n.Select();
				trussView.infoBox.setText(n.getText());
				trussView.infoBox.isVisible = true;

				mActionMode = MainActivity.this.startSupportActionMode(mActionModeCallback);
			}
			return true;
		}

		@Override
		public boolean onNodeLongPressed(Node n)
		{
			ShowEditNodeDialog(n);
			return false;
		}

		@Override
		public boolean onMemberPressed(Member m)
		{
			if (uiState == UIState.NONE)
			{
				if (mActionMode != null) mActionMode.finish();
				Global.CurrentTruss.UnselectAll();
				m.Select();
				trussView.infoBox.setText(m.getText(getApplicationContext()));
				trussView.infoBox.isVisible = true;
				mActionMode = MainActivity.this.startSupportActionMode(mActionModeCallback);
			}

			return true;
		}

		@Override
		public boolean onMemberlongPressed(Member m)
		{
			return false;
		}


		@Override
		public boolean onPointLoadPressed(Load dl)
		{
			if (uiState == UIState.NONE)
			{
				if (mActionMode != null) mActionMode.finish();
				Global.CurrentTruss.UnselectAll();
				dl.Select();
				trussView.infoBox.setText(dl.getText(getApplicationContext()));
				trussView.infoBox.isVisible = true;
				mActionMode = MainActivity.this.startSupportActionMode(mActionModeCallback);
			}
			return true;
		}


		@Override
		public void onTouchWithoutSelection()
		{
			if (uiState == UIState.NONE)
			{
				Global.CurrentTruss.UnselectAll();
				if (mActionMode != null) mActionMode.finish();
			}
		}

		@Override
		public void onLongTouchWithoutSelection()
		{

		}
	};
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			trussView.Update();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.trussView = (TrussView) findViewById(R.id.frameView);
		startService(new Intent(this, UpdateTimer.class));

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		Global.CurrentTruss = new Truss(null);
		Global.trussIO = new TrussIO(this);

		try
		{
			Global.CurrentTruss = Global.trussIO.OpenCache();
			trussView.origin = new Vector2D(Double.parseDouble(Global.trussIO.GetAttribute("Ox")), Double.parseDouble(Global.trussIO.GetAttribute("Oy")));
			trussView.zoom = Float.parseFloat(Global.trussIO.GetAttribute("zoom"));
		} catch (Exception ex)
		{
			ex.printStackTrace();
			Global.CurrentTruss = new Truss(null);
		}


		if (savedInstanceState != null)
		{
			trussView.origin = new Vector2D(savedInstanceState.getDouble("Ox"), savedInstanceState.getDouble("Oy"));
			trussView.zoom = savedInstanceState.getFloat("zoom");

		}

        /*
		Timer rTimer = new Timer();
		TimerTask rTask = new TimerTask()
		{
			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						trussView.Update();
					}
				});
			}
		};
		rTimer.schedule(rTask, 10, 50);
*/
		trussView.setSelectListener(trussItemSelectListener);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), new IntentFilter(UpdateTimer.UPDATE));
	}

	@Override
	protected void onStop()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
	protected void onPause()
	{

		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		//stopService(intent);

		Global.trussIO.SaveAsCache(Global.CurrentTruss);
		Global.trussIO.WriteAttribute("Ox", trussView.origin.X + "");
		Global.trussIO.WriteAttribute("Oy", trussView.origin.Y + "");
		Global.trussIO.WriteAttribute("zoom", trussView.zoom + "");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//startService(intent);
		//registerReceiver(broadcastReceiver, new IntentFilter(Timer.BROADCAST_ACTION));

		try
		{
			Global.CurrentTruss = Global.trussIO.OpenCache();
			trussView.origin = new Vector2D(Double.parseDouble(Global.trussIO.GetAttribute("Ox")), Double.parseDouble(Global.trussIO.GetAttribute("Oy")));
			trussView.zoom = Float.parseFloat(Global.trussIO.GetAttribute("zoom"));
		} catch (Exception ex)
		{
			Global.CurrentTruss = new Truss(null);
		}

	}


	@Override
	public void onNavigationDrawerItemSelected(int position)
	{
		if (Global.CurrentTruss == null) return;
		if (Global.trussIO == null) return;
		if (mNavigationDrawerFragment != null)
		{
			if (mNavigationDrawerFragment.isDrawerOpen())
			{
				if (position == 0)
				{
					Global.CurrentTruss.Reset();
					trussView.infoBox.isVisible = false;

				} else if (position == 1)
				{
					ShowOpenTrussDialog();
				} else if (position == 2)
				{
					ShowSaveTrussDialog();
				} else if (position == 3)
				{
					ShowAboutActivity();
				}
			}
		}
	}


	private void ShowAboutActivity()
	{
		Intent in = new Intent(this, AboutActivity.class);
		Global.trussIO.SaveAsCache(Global.CurrentTruss);
		Global.trussIO.WriteAttribute("Ox", trussView.origin.X + "");
		Global.trussIO.WriteAttribute("Oy", trussView.origin.Y + "");
		Global.trussIO.WriteAttribute("zoom", trussView.zoom + "");
		startActivity(in);
	}

	public void ShowOpenTrussDialog()
	{
		OpenDialog dlg = new OpenDialog();
		dlg.show(MainActivity.this.getFragmentManager(), "open");
		//trussView.ZoomExtents();
	}

	public void ShowSaveTrussDialog()
	{
		if (Global.CurrentTruss.Name == null || Global.CurrentTruss.Name.equals(Global.TRUSS_DATA_CACHE))
		{
			SaveDialog dlg = new SaveDialog();
			dlg.show(MainActivity.this.getFragmentManager(), "save");
		} else
		{
			Global.trussIO.Save(Global.CurrentTruss);
			Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
		}
	}

	public void restoreActionBar()
	{
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setTitle("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (!mNavigationDrawerFragment.isDrawerOpen())
		{
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.action_add_node)
		{
			setUiState(UIState.ADD_NODE);
			ShowAddNodeDialog();
			return true;
		} else if (id == R.id.action_add_member)
		{
			setUiState(UIState.ADD_MEMBER);
			return true;

		} else if (id == R.id.action_add_hinge)
		{
			setUiState(UIState.ADD_HINGE_SUPPORT);
			return true;
		} else if (id == R.id.action_add_roller)
		{
			setUiState(UIState.ADD_ROLLER_SUPPORT);
			return true;
		} else if (id == R.id.action_add_roller_horz)
		{
			setUiState(UIState.ADD_HROLLER_SUPPORT);
			return true;
		} else if (id == R.id.action_add_load)
		{
			setUiState(UIState.ADD_POINT_LOAD);
			return true;
		} else if (id == R.id.action_zoom_extents)
		{
			trussView.ZoomExtents();
		} else if (id == R.id.action_solve)
		{
			Global.CurrentTruss.solve();
		} else if (id == R.id.action_remove_support)
		{
			setUiState(UIState.DELETE_SUPPORT);
		} else if (id == R.id.action_about)
		{
			ShowAboutActivity();
		} else if (id == R.id.action_settings)
		{
			Intent in = new Intent(this, SettingsActivity.class);
			Global.trussIO.SaveAsCache(Global.CurrentTruss);
			Global.trussIO.WriteAttribute("Ox", trussView.origin.X + "");
			Global.trussIO.WriteAttribute("Oy", trussView.origin.Y + "");
			Global.trussIO.WriteAttribute("zoom", trussView.zoom + "");
			startActivity(in);
		}

		return super.onOptionsItemSelected(item);
	}

	public void ShowAddNodeDialog()
	{
		AddNodeDialog dlg = new AddNodeDialog();
		dlg.show(MainActivity.this.getFragmentManager(), "add_load");
		setUiState(UIState.NONE);
	}

	public void ShowEditNodeDialog(final Node n)
	{
		AddNodeDialog dlg = new AddNodeDialog();
		dlg.setNode(n);
		dlg.show(MainActivity.this.getFragmentManager(), "edit_node");
		setUiState(UIState.NONE);
	}

	public void showEditMemberDialog(final Member m)
	{
		EditMemberDialog dlg = new EditMemberDialog();
		dlg.setMember(m);
		dlg.show(MainActivity.this.getFragmentManager(), "edit_member");
		setUiState(UIState.NONE);
	}

	private void setUiState(UIState state)
	{


		if (state == UIState.ADD_HINGE_SUPPORT || state == UIState.ADD_ROLLER_SUPPORT || state == UIState.ADD_HROLLER_SUPPORT || state == UIState.ADD_POINT_LOAD)
		{
			if (Global.CurrentTruss.Nodes.size() <= 0)
			{
				Toast.makeText(this, "No nodes found", Toast.LENGTH_SHORT).show();
				uiState = UIState.NONE;
				return;
			} else
			{
				Toast.makeText(this, "Select node", Toast.LENGTH_SHORT).show();
			}
		} else if (state == UIState.ADD_MEMBER)
		{
			if (Global.CurrentTruss.Nodes.size() <= 1)
			{
				Toast.makeText(this, "Insufficient nodes", Toast.LENGTH_SHORT).show();
				uiState = UIState.NONE;
				return;
			} else
			{
				Toast.makeText(this, "Select nodes", Toast.LENGTH_SHORT).show();
			}
		} else if (state == UIState.DELETE_SUPPORT)
		{
			if (Global.CurrentTruss.HingeSupports.size() + Global.CurrentTruss.RollerSupports.size() < 0)
			{
				Toast.makeText(this, "No supports present", Toast.LENGTH_SHORT).show();
				uiState = UIState.NONE;
				return;
			} else
			{
				Toast.makeText(this, "Select supported node", Toast.LENGTH_SHORT).show();
			}
		}
		uiState = state;

	}

	public void selectNodeForElement(Node n)
	{
		if (MemberNodeID == -1)
		{
			MemberNodeID = n.id;
		} else if (MemberNodeID != n.id)
		{
			double E, A;
			if (Global.CurrentTruss.Members.size() > 0)
			{
				E = Global.CurrentTruss.Members.get(Global.CurrentTruss.Members.size() - 1).Emod;
				A = Global.CurrentTruss.Members.get(Global.CurrentTruss.Members.size() - 1).Area;
			} else
			{
				E = 200;
				A = 100;
			}
			Global.CurrentTruss.AddMember(MemberNodeID, n.id, E, A);
			MemberNodeID = -1;
			uiState = UIState.NONE;
			Global.CurrentTruss.UnselectAll();
		}
	}
}
