package com.ithinkrok.yellowquest.util;

public class Box {

	public double sx, sy, ex, ey;

	public Box(double sx, double sy, double ex, double ey) {
		this.sx = Math.min(sx, ex);
		this.sy = Math.min(sy, ey);
		this.ex = Math.max(ex, sx);
		this.ey = Math.max(ey, sy);
	}

	public Box include(double x, double y) {
		double mix = this.sx;
		double miy = this.sy;
		double max = this.ex;
		double may = this.ey;
		if (x < 0) mix += x;
		else if (x > 0) max += x;
		if (y < 0) miy += y;
		else if (y > 0) may += y;
		return new Box(mix, miy, max, may);
	}
	
	public Box include(double x, double y, Box out) {
		out.sx = this.sx;
		out.sy = this.sy;
		out.ex = this.ex;
		out.ey = this.ey;
		if (x < 0) out.sx += x;
		else if (x > 0) out.ex += x;
		if (y < 0) out.sy += y;
		else if (y > 0) out.ey += y;
		return out;
	}
	
	public void set(double sx, double sy, double ex, double ey){
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;
	}
	
	public Box expand(double x, double y) {
	    return new Box(this.sx - x, this.sy - y, this.ex + x, this.ey + y);
	}

	public Box move(double x, double y) {
	    return new Box(this.sx + x, this.sy + y, this.ex + x, this.ey + y);
	}
	
	public Box move(double x, double y, Box out) {
		out.sx = this.sx + x;
		out.sy = this.sy + y;
		out.ex = this.ex + x;
		out.ey = this.ey + y;
	    return out;
	}
	
	public double calcXOffset(Box box, double x) {
	    if (box.ey <= this.sy || box.sy >= this.ey) return x;
	    if (x > 0 && box.ex <= this.sx) return Math.min(this.sx - box.ex, x);
	    if (x < 0 && box.sx >= this.ex) return Math.max(this.ex - box.sx, x);
	    return x;
	}

	public double calcYOffset(Box box, double y) {
	    if (box.ex <= this.sx || box.sx >= this.ex) return y;
	    if (y > 0 && box.ey <= this.sy) return Math.min(this.sy - box.ey, y);
	    if (y < 0 && box.sy >= this.ey) return Math.max(this.ey - box.sy, y);
	    return y;
	}

	public boolean intersects(Box box) {
	    if (box == null) return false;
	    if (box.ex < this.sx || box.sx > this.ex) return false;
	    if (box.ey < this.sy || box.sy > this.ey) return false;
	    return true;
	};
	
}
