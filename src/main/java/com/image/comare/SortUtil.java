package com.image.comare;

import java.util.ArrayList;
import java.util.List;

import com.image.comare.objects.ChangesPoly;
import com.image.comare.objects.Point;

public class SortUtil {

	List<List<Point>> clusters = new ArrayList<>();
	List<ChangesPoly> changes = new ArrayList<>();
	
	public List<List<Point>> getClusters() {
		return clusters;
	}

	public List<ChangesPoly> getChanges() {
		return changes;
	}

	public void findChangesPoly(List<Point> changePoints) {
		   for (Point point : changePoints) {
			   if (point.isAssigned()) {
				   continue;
			   }
			   point.setAssigned(true);
			   findNeighborhoods(point, changePoints);
		   }
		   
		   int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		   int maxX = 0, maxY = 0;
		   
		   for (List<Point> list : clusters) {
			   for (Point point : list) {
				   if (point.getX() < minX) {
					   minX = point.getX();
				   } else if (point.getX() > maxX) {
					   maxX = point.getX();
				   }
				   
				   if (point.getY() < minY) {
					   minY = point.getY();
				   } else if (point.getY() > maxY) {
					   maxY = point.getY();
				   }
			   }
			   changes.add(new ChangesPoly(minX, minY, maxX, maxY));
			   
			   minX = Integer.MAX_VALUE;
			   minY = Integer.MAX_VALUE;
			   maxX = 0;
			   maxY = 0;
		   }
		   
	   }
	   
	   public void findNeighborhoods(Point first, List<Point> changePoints) {
		   List<Point> cluster = new ArrayList<Point>();
		   cluster.add(first);
		   for (Point point : changePoints) {
			   if (!point.isAssigned()) {
				   int xDiss = Math.abs(point.getX() - first.getX());
				   int yDiss = Math.abs(point.getY() - first.getY());
				   if (xDiss < 100 && yDiss < 100) {
					   point.setAssigned(true);
					   cluster.add(point);
				   }
			   }
		   }
		   clusters.add(cluster);
	   }
	   
}
