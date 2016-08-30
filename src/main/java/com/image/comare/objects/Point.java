package com.image.comare.objects;

public class Point {
   private int x;
   private int y;
   private boolean assigned = false;

   public Point(int x, int y) {
       this.x = x;
       this.y = y;
   }

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isAssigned() {
		return assigned;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}
	
}
