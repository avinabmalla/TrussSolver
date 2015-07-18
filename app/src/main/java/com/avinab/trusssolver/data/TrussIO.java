package com.avinab.trusssolver.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.data.supports.HingeSupport;
import com.avinab.trusssolver.data.supports.RollerSupport;

import java.io.File;


/**
 * Avinab
 * 12/6/2014.
 */
public class TrussIO
{
	public Database database;
	public SQLiteDatabase db;
	Context ctx;

	public TrussIO(Context ctx)
	{
		this.ctx = ctx;
	}


	public Truss OpenCache() throws Exception
	{
		String path = ctx.getApplicationInfo().dataDir + "/databases/" + Global.TRUSS_DATA_CACHE;
		File f = new File(path);
		if (f.exists())
		{
			Truss frm = Open(Global.TRUSS_DATA_CACHE);
			frm.Name = GetAttribute("name");
			return frm;
		} else
		{
			throw new Exception("No Cache Found");
		}
	}

	public void WriteAttribute(String key, String value)
	{
		ContentValues cv = new ContentValues();
		cv.put("key", key);
		cv.put("value", value);
		int count = db.update("attrs", cv, "key='" + key + "'", null);
		if (count == 0)
		{
			db.insert("attrs", null, cv);
		}
	}

	public String GetAttribute(String key)
	{
		Cursor cur = db.rawQuery("SELECT * FROM attrs where key='" + key + "';", null);
		if (cur.moveToFirst())
		{
			return cur.getString(cur.getColumnIndex("value"));
		}
		return null;
	}


	public void SaveAsCache(Truss truss)
	{
		if (database != null) database.close();
		database = new Database(ctx, Global.TRUSS_DATA_CACHE);
		db = database.getWritableDatabase();
		if (truss.Name == null)
		{
			truss.Name = Global.TRUSS_DATA_CACHE;
		}
		WriteAttribute("name", truss.Name);
		Save(truss);
	}


	public Truss Open(String name)
	{
		if (database != null) database.close();
		database = new Database(ctx, name);
		db = database.getWritableDatabase();
		Truss truss = new Truss(name);

		truss.Reset();
		Cursor cur;

		cur = db.rawQuery("SELECT * FROM nodes;", null);
		if (cur.moveToFirst())
		{
			do
			{
				int id = cur.getInt(cur.getColumnIndex("node_id"));
				double x = cur.getDouble(cur.getColumnIndex("x"));
				double y = cur.getDouble(cur.getColumnIndex("y"));
				String type = cur.getString(cur.getColumnIndex("type"));
				truss.Nodes.add(new Node(truss, id, x, y));
			} while (cur.moveToNext());
		}


		cur = db.rawQuery("SELECT * FROM members;", null);
		if (cur.moveToFirst())
		{
			do
			{
				int id = cur.getInt(cur.getColumnIndex("member_id"));
				int start_node = cur.getInt(cur.getColumnIndex("start_node"));
				int end_node = cur.getInt(cur.getColumnIndex("end_node"));
				double E = cur.getDouble(cur.getColumnIndex("E"));
				double A = cur.getDouble(cur.getColumnIndex("A"));
				Member m = new Member(truss, id, start_node, end_node, E, A);
				truss.Members.add(m);
			} while (cur.moveToNext());
		}


		cur = db.rawQuery("SELECT * FROM supports;", null);
		if (cur.moveToFirst())
		{
			do
			{
				int node = cur.getInt(cur.getColumnIndex("node"));
				String type = cur.getString(cur.getColumnIndex("type"));
				Node n = truss.getNode(node);
				if (type.equals("HINGE"))
				{
					truss.AddHingeSupport(n);
				} else if (type.equals("H_ROLLER"))
				{
					truss.AddRollerSupport(n, true);
				} else if (type.equals("V_ROLLER"))
				{
					truss.AddRollerSupport(n, false);
				}
			} while (cur.moveToNext());
		}


		cur = db.rawQuery("SELECT * FROM point_loads;", null);
		if (cur.moveToFirst())
		{
			do
			{
				int node = cur.getInt(cur.getColumnIndex("node_id"));
				double mag = cur.getDouble(cur.getColumnIndex("magnitude"));
				double ang = cur.getDouble(cur.getColumnIndex("angle"));
				truss.addPointLoad(truss.getNode(node), mag, ang);
			} while (cur.moveToNext());
		}


		//database.close();

		return truss;
	}

	public void Save(Truss truss)
	{

		if (database != null) database.close();
		database = new Database(ctx, truss.Name);
		db = database.getWritableDatabase();

		//Clear Tables
		db.execSQL("DELETE FROM nodes;");
		db.execSQL("DELETE FROM members;");
		db.execSQL("DELETE FROM supports;");
		db.execSQL("DELETE FROM point_loads;");

		WriteAttribute("name", truss.Name);
		//INSERT
		for (Node n : truss.Nodes)
		{
			ContentValues cv = new ContentValues();
			cv.put("node_id", n.id);
			cv.put("x", n.Location.X);
			cv.put("y", n.Location.Y);
			db.insert("nodes", null, cv);
		}

		for (Member m : truss.Members)
		{
			ContentValues cv = new ContentValues();
			cv.put("member_id", m.id);
			cv.put("start_node", m.getFromNode().id);
			cv.put("end_node", m.getToNode().id);
			cv.put("E", m.Emod);
			cv.put("A", m.Area);
			db.insert("members", null, cv);
		}


		for (HingeSupport h : truss.HingeSupports)
		{
			ContentValues cv = new ContentValues();
			cv.put("node", h.getNode().id);
			cv.put("type", "HINGE");
			db.insert("supports", null, cv);
		}


		for (RollerSupport r : truss.RollerSupports)
		{
			ContentValues cv = new ContentValues();
			cv.put("node", r.getNode().id);
			if (r.isHorizontal())
			{
				cv.put("type", "H_ROLLER");
			} else
			{
				cv.put("type", "V_ROLLER");
			}
			db.insert("supports", null, cv);
		}

		for (Load l : truss.PointLoads)
		{
			ContentValues cv = new ContentValues();
			cv.put("node_id", l.loadNode);
			cv.put("magnitude", l.getMagnitude());
			cv.put("angle", l.getAngle());
			db.insert("point_loads", null, cv);
		}
	}


	public class Database extends SQLiteOpenHelper
	{

		public static final int version = 3;

		public Database(Context context, String name)
		{
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			createDefaultDatabase(db);
			Log.i("database", db.getPath());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(Database.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS attrs;");
			db.execSQL("DROP TABLE IF EXISTS nodes;");
			db.execSQL("DROP TABLE IF EXISTS members;");
			db.execSQL("DROP TABLE IF EXISTS supports;");
			db.execSQL("DROP TABLE IF EXISTS point_loads;");
			onCreate(db);

		}

		private void createDefaultDatabase(SQLiteDatabase db)
		{
			String createAttrs = "CREATE TABLE attrs(id integer primary key autoincrement,key text,value text);";
			String createNodes = "CREATE TABLE nodes(id integer primary key autoincrement,node_id integer, x number,y number,type text);";
			String createMember = "CREATE TABLE members(id integer primary key autoincrement,member_id integer, start_node integer,end_node integer,E number, A number);";
			String createSupports = "CREATE TABLE supports(id integer primary key autoincrement, node integer,type Text);";
			String createPointLoads = "CREATE TABLE point_loads(id integer primary key autoincrement,node_id integer,magnitude number,angle number);";

			db.execSQL(createAttrs);
			db.execSQL(createNodes);
			db.execSQL(createMember);
			db.execSQL(createSupports);
			db.execSQL(createPointLoads);

		}
	}

}
