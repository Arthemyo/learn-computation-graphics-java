package game.test.com.game.voxel.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import game.test.com.game.voxel.utils.Utils;

public class Shader {

	private int programId;
	private final int vertexShaderId;
	private final int fragmentShaderId;

    public Shader(String vertexShaderSourcePath, String fragmentShaderSourcePath) throws IOException {

		this.programId = GL30.glCreateProgram();

		InputStream inputStream = new FileInputStream(vertexShaderSourcePath);
        String vertexShaderSource = Utils.readFile(inputStream);

		InputStream inputStream2 = new FileInputStream(fragmentShaderSourcePath);
        String fragmentShaderSource = Utils.readFile(inputStream2);

		this.vertexShaderId = this.createShader(vertexShaderSource, GL30.GL_VERTEX_SHADER);
		this.fragmentShaderId = this.createShader(fragmentShaderSource, GL30.GL_FRAGMENT_SHADER);

		GL30.glLinkProgram(programId);

		if (GL30.glGetProgrami(programId, GL30.GL_LINK_STATUS) == 0) {
			throw new RuntimeException("Error linking Shader code: " + GL30.glGetProgramInfoLog(programId, 1024));
		}

		GL30.glValidateProgram(programId);
		if (GL30.glGetProgrami(programId, GL30.GL_VALIDATE_STATUS) == 0) {
			throw new RuntimeException("Error validating Shader code: " + GL30.glGetProgramInfoLog(programId, 1024));
		}
	}

	public int createShader(String shaderCode, int shaderType) {
		int shaderId = GL30.glCreateShader(shaderType);

		if (shaderId == 0) {
			System.out.println("SHADER ID EQUAL 0");
			throw new RuntimeException("Error creating shader. Type: " + shaderType);
		}

		GL30.glShaderSource(shaderId, shaderCode);
		GL30.glCompileShader(shaderId);

		if (GL30.glGetShaderi(shaderId, GL30.GL_COMPILE_STATUS) == 0) {
			System.out.println("GL_COMPILE STATUS EQUAL 0");
			throw new RuntimeException("Error compiling Shader code: " + GL30.glGetShaderInfoLog(shaderId, 1024));
		}

		if (programId == 0) {
			throw new RuntimeException("Could not create Shader");
		}

		GL30.glAttachShader(programId, shaderId);

		return shaderId;
	}

	public void bind() {
		GL30.glUseProgram(programId);
	}

	public void unbind() {
		GL30.glUseProgram(0);
	}

	public void setBool(String name, int value) {
		GL30.glUniform1i(GL30.glGetUniformLocation(programId, name), value);
	}

	public void setInt(String name, int value) {
		GL30.glUniform1i(GL30.glGetUniformLocation(programId, name), value);
	}

	public void setFloat(String name, float value) {
		GL30.glUniform1f(GL30.glGetUniformLocation(programId, name), value);
	}

	public void setMat4(String name, Matrix4f mat4) {
		int mat4Loc = GL20.glGetUniformLocation(programId, name);
		FloatBuffer mat4Buffer = BufferUtils.createFloatBuffer(16);

		mat4.get(mat4Buffer);

		GL20.glUniformMatrix4fv(mat4Loc, false, mat4Buffer);
	}

	public void setVec3(String name, Vector3f vec3) {
		int vec3Loc = GL20.glGetUniformLocation(programId, name);
		FloatBuffer vec3Buffer = BufferUtils.createFloatBuffer(3);

		vec3.get(vec3Buffer);

		GL20.glUniform3fv(vec3Loc, vec3Buffer);
	}

	public int getProgramId() {
		return programId;
	}

	public int getVertexShaderId() {
		return vertexShaderId;
	}

	public int getFragmentShaderId() {
		return fragmentShaderId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public void deleteShader() {
		GL30.glDeleteShader(vertexShaderId);
		GL30.glDeleteShader(fragmentShaderId);
	}

	public void setVec2(String name, Vector2f vec2) {
		int vec2Loc = GL20.glGetUniformLocation(programId, name);
		FloatBuffer vec2Buffer = BufferUtils.createFloatBuffer(2);

		vec2.get(vec2Buffer);

		GL20.glUniform2fv(vec2Loc, vec2Buffer);
	}
}
