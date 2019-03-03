package polichrome;

public class Vector2 {

	public float x, y;
	
	public Vector2(Vector2 v) {
		x = v.x;
		y = v.y;
	}
	
	public Vector2(float f) {
		x = f;
		y = f;
	}
	
	public Vector2() {
		this(0);
	}
	
	public Vector2(float xx, float yy) {
		x = xx;
		y = yy;
	}
	
	public float Distance(Vector2 v) {
		return (float)Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y));
	}
	
	public float DistanceSq(Vector2 v) {
		return ((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y));
	}
	
	public Vector2 Add(Vector2 v) {
		return new Vector2(x + v.x, y + v.y);
	}
	
	public Vector2 Subtract(Vector2 v) {
		return new Vector2(x - v.x, y - v.y);
	}
	
	public Vector2 Multiply(float f) {
		return new Vector2(x * f, y * f);
	}
	
	public Vector2 Multiply(Vector2 f) {
		return new Vector2(x * f.x, y * f.y);
	}
	
	public Vector2 Lerp(Vector2 v, float f) {
		return new Vector2(
				x * (1.0f - f) + v.x * f,
				y * (1.0f - f) + v.y * f);
				
	}
	
	public float Length() {
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public float Dot(Vector2 other) {
		return x * other.x + y * other.y;
	}
	
	public Vector2 Normalize() {
		float l = Length();
		return new Vector2(x / l, y / l);
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Vector2))
			return false;
		Vector2 vo = (Vector2)o;
		if(vo.x != x || vo.y != y)
			return false;
		return true;
	}
}
