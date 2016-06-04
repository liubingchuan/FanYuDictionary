package entity;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import org.springframework.data.annotation.Id;

public class Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Expose
	String id;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
}