package org.cakelab.blender.generator.code;

import java.io.IOException;

import org.cakelab.blender.doc.DocumentationProvider;
import org.cakelab.blender.generator.type.CField;
import org.cakelab.blender.generator.type.CStruct;
import org.cakelab.blender.generator.type.CType;
import org.cakelab.blender.generator.type.CType.CTypeType;
import org.cakelab.blender.generator.type.JavaType;
import org.cakelab.blender.generator.type.JavaType.JTypeType;
import org.cakelab.blender.model.DNAPointer;
import org.cakelab.blender.model.int64;

public abstract class FieldVisitor extends CodeGenerator {

	private DocumentationProvider docs;
	private String fielddoc;
	protected ClassGenerator classgen;

	protected FieldVisitor(ClassGenerator classGenerator, int initialIndent) {
		super(initialIndent);
		this.classgen = classGenerator;
		docs = classGenerator.getDocs();
	}


	
	public void visitField(long offset32, long offset64, CStruct struct, CField field, JavaType jtype) throws IOException {
		reset();
		fielddoc = docs.getFieldDoc(struct.getSignature(), field.getName());
		switch(field.getType().getTypetype()) {
		case TYPE_ARRAY:
			visitArray(offset32, offset64, field, jtype);
			break;
		case TYPE_FUNCTION_POINTER:
			break;
		case TYPE_POINTER:
			visitPointer(offset32, offset64, field, jtype);
			break;
		case TYPE_SCALAR:
			visitScalar(offset32, offset64, field, jtype);
			break;
		case TYPE_STRUCT:
			visitStruct(offset32, offset64, field, jtype);
			break;
		case TYPE_VOID:
			break;
		default:
			break;
		}
	}


	protected String getFieldDescriptorName(String name) {
		return "__DNA__FIELD__" + name;
	}


	protected abstract void visitArray(long offset32, long offset64, CField field, JavaType jtype) throws IOException;

	protected abstract void visitPointer(long offset32, long offset64, CField field, JavaType jtype) throws IOException;

	protected abstract void visitScalar(long offset32, long offset64, CField field, JavaType jtype) throws IOException;

	protected abstract void visitStruct(long offset32, long offset64, CField field, JavaType jtype) throws IOException;

	protected String getFieldDoc() {
		return fielddoc;
	}
	

	protected String getFieldType(CType ctype,
			JavaType jtype) throws IOException {
		String returnType = jtype.getName();
		if (ctype.getTypetype() == CTypeType.TYPE_POINTER || ctype.getTypetype() == CTypeType.TYPE_ARRAY) {
			returnType += getTemplateParameter(ctype.getReferencedType(), jtype.getReferencedType());
		}
		return returnType;
	}


	protected String getTemplateParameter(CType ctype, JavaType jtype) throws IOException {
		String templateParam = "?";
		switch(ctype.getTypetype()) {
		case TYPE_ARRAY:
			templateParam = jtype.getName() + getTemplateParameter(ctype.getReferencedType(), jtype.getReferencedType());
			break;
		case TYPE_FUNCTION_POINTER:
			templateParam = jtype.getName();
			break;
		case TYPE_POINTER:
			classgen.addImport(DNAPointer.class);
			templateParam = jtype.getName() + getTemplateParameter(ctype.getReferencedType(), jtype.getReferencedType());
			break;
		case TYPE_SCALAR:
			templateParam = getScalarJavaObjectType(ctype, jtype).getSimpleName();
			break;
		case TYPE_STRUCT:
			templateParam = jtype.getName();
			break;
		case TYPE_VOID:
			templateParam = jtype.getName();
			break;
		default:
			throw new IOException("unrecognized type in field '" + ctype.getSignature() + "'");
		}
		return "<" + templateParam + ">";
	}


	protected Class<?> getScalarJavaObjectType(CType ctype, JavaType jtype) throws IOException {
		if (jtype.getName().equals(byte.class.getName())) {
			return Byte.class;
		} else if (jtype.getName().equals(short.class.getName())) {
			return Short.class;
		} else if (jtype.getName().equals(int.class.getName())) {
			return Integer.class;
		} else if (jtype.getName().equals(long.class.getName())) {
			if (ctype.getSignature().contains("int64")) {
				return int64.class;
			} else {
				return Long.class;
			}
		} else if (jtype.getName().equals(int64.class.getName())) {
			return int64.class;
		} else if (jtype.getName().equals(float.class.getName())) {
			return Float.class;
		} else if (jtype.getName().equals(double.class.getName())) {
			return Double.class;
		} else {
			throw new IOException("unrecognized type '" + jtype.getName() + "'");
		}
	}

	protected String getTypeList(JavaType jtype,
			CType ctype) throws IOException {
		StringBuffer s = new StringBuffer();
		s.append("new Class[]{");
		JavaType target = jtype;
		s.append(getClassTypeName(ctype, target)).append(".class");
		while(target.getReferencedType() != null) {
			target = target.getReferencedType();
			s.append(", ");
			s.append(getClassTypeName(ctype, target)).append(".class");
		}
		s.append("}");
		
		return s.toString();
	}


	protected String getClassTypeName(CType ctype, JavaType jtype) throws IOException {
		if (jtype.getTypetype() == JTypeType.TYPE_SCALAR) {
			return getScalarJavaObjectType(ctype, jtype).getSimpleName();
		} else {
			return jtype.getName();
		}
	}

}
