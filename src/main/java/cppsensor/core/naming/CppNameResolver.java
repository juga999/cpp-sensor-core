/**
 * Copyright (C) 2016 Julien Gaston
 * cpp-sensor@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package cppsensor.core.naming;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.parser.Keywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jgaston
 *
 */
public class CppNameResolver {

  private static final Logger log = LoggerFactory.getLogger("NameResolver");

  public static final String NAMESPACE_DELIMITER = new String(Keywords.cpCOLONCOLON);

  private static final String ANONYMOUS_TYPE_PATTERN = ".*\\{.+\\}$";

  private static final String TEMPLATE_PARAMS_PATTERN = "<.*>[\\s]*[\\*]*$";

  private boolean logBindingIssues = true;

  private ICppNameIssueHandler issueHandler = null;

  public CppNameResolver(boolean warnBindingIssues) {
    logBindingIssues = warnBindingIssues;
  }

  public void setIssueHandler(ICppNameIssueHandler handler) {
    issueHandler = handler;
  }

  public static String getCalledFunctionName(IASTFunctionCallExpression fctCallExp) {
    String name = "";

    IASTExpression exp = fctCallExp.getFunctionNameExpression();
    if (exp instanceof IASTIdExpression) {
      name = getGlobalNsFunctionName((IASTIdExpression)exp);
    } else if (exp instanceof IASTFieldReference) {
      name = getMemberFunctionName((IASTFieldReference)exp);
    }

    return name;
  }

  public String getResolvedName(IBinding binding) {
    if (binding == null || binding.getName() == null) {
      return "";
    }

    String resolvedName = "";

    if (binding instanceof IProblemBinding) {
      resolvedName = getName((IProblemBinding)binding);
    } else if (binding instanceof ITypedef) {
      resolvedName = getName((ITypedef)binding);
    } else if (binding instanceof ICPPBinding) {
      resolvedName = getName((ICPPBinding)binding);
    } else {
      resolvedName = binding.getName();
    }

    return resolvedName;
  }

  private String getName(IProblemBinding binding) {
    String resolvedName = "";

    IASTNode node = binding.getASTNode();
    IASTNode parent = node.getParent();
    if (node instanceof IASTNamedTypeSpecifier) {
      resolvedName = String.copyValueOf(
          ((IASTNamedTypeSpecifier) node).getName().toCharArray());
    } else if (parent instanceof ICPPASTQualifiedName) {
      resolvedName = String.copyValueOf(
          ((ICPPASTQualifiedName) parent).toCharArray());
    } else if (node instanceof IASTName) {
      resolvedName = String.copyValueOf(
          ((IASTName) node).toCharArray());
    } else {
      resolvedName = binding.getName();
    }

    if (issueHandler != null) {
      issueHandler.onNameIssue(binding, resolvedName);
    } else if (logBindingIssues) {
      log.warn(binding.getMessage());
    }

    return resolvedName;
  }

  private String getName(ITypedef binding) {
    return ASTTypeUtil
        .getType(binding)
        .replaceFirst(TEMPLATE_PARAMS_PATTERN, "")
        .replaceFirst(ANONYMOUS_TYPE_PATTERN, binding.getName());
  }

  private String getName(ICPPBinding binding) {
    String resolvedName = "";

    String tmpName = ASTTypeUtil.getQualifiedName(binding);
    resolvedName = tmpName.replaceFirst(TEMPLATE_PARAMS_PATTERN, "");
    if (resolvedName.matches(ANONYMOUS_TYPE_PATTERN)) {
      resolvedName = "";
    }

    return resolvedName;
  }

  private static String getGlobalNsFunctionName(IASTIdExpression idExp) {
    IASTName name = idExp.getName();
    if (name == null) {
      return "";
    }

    IBinding binding = name.resolveBinding();
    if (binding instanceof ICPPBinding) {
      return ASTTypeUtil.getQualifiedName((ICPPBinding)binding);
    } else {
      return name.toString();
    }
  }

  private static String getMemberFunctionName(IASTFieldReference fieldRef) {
    String result = "";

    IASTExpression fieldOwner = fieldRef.getFieldOwner();
    if (fieldOwner instanceof ICPPASTInitializerClause) {
      ICPPASTInitializerClause clause = (ICPPASTInitializerClause)fieldOwner;
      IType type = clause.getEvaluation().getTypeOrFunctionSet(fieldOwner);
      String normalizedTypename = ASTTypeUtil.getType(type);
      String typeName =
          normalizedTypename.replaceFirst(TEMPLATE_PARAMS_PATTERN, "");
      result = buildMemberFunctionName(typeName, fieldRef);
    } else if (fieldOwner instanceof IASTIdExpression) {
      IType type = fieldOwner.getExpressionType();
      if (type instanceof ITypedef) {
        ITypedef typedef = (ITypedef)type;
        result = buildMemberFunctionName(typedef.getName(), fieldRef);
      } else if (type instanceof ICompositeType) {
        ICompositeType compositeType = (ICompositeType)type;
        result = buildMemberFunctionName(compositeType.getName(), fieldRef);
      }
    }

    return result;
  }

  private static String buildMemberFunctionName(String typeName, IASTFieldReference fieldRef) {
    IASTName name = fieldRef.getFieldName();
    if (name == null) {
      return "";
    } else {
      return String.format("%s%s%s", typeName, NAMESPACE_DELIMITER, name.toString());
    }
  }

}
