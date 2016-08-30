package com.image.comare;

import java.util.ArrayList;
import java.util.List;

import com.image.comare.objects.ChangesPoly;
import com.image.comare.objects.Point;

public final class SortUtil {

    private SortUtil() {}

    public static List<ChangesPoly> findChangesPoly(List<Point> changePoints) {
        List<List<Point>> clusters = new ArrayList<>();
        for (Point point : changePoints) {
            if (point.isAssigned()) {
                continue;
            }
            point.setAssigned(true);
            clusters.add(findNeighborhoods(point, changePoints));
        }
        return getClustersPoly(clusters);
    }

    private static List<ChangesPoly> getClustersPoly(List<List<Point>> clusters) {
        List<ChangesPoly> changes = new ArrayList<>();
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
        return changes;
    }

    private static List<Point> findNeighborhoods(Point first, List<Point> changePoints) {
        List<Point> cluster = new ArrayList<>();
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
        return cluster;
    }
}