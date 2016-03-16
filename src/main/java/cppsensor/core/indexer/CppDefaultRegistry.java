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

package cppsensor.core.indexer;

import java.io.InputStream;
import java.util.ResourceBundle;

import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.spi.IRegistryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jgaston
 *
 */
final class CppDefaultRegistry implements IRegistryProvider, IExtensionRegistry {

  private static final Logger log = LoggerFactory.getLogger("Registry");

  private static final String[] EMPTY_NAMESPACES =
      new String[] {};

  private static final IExtension[] EMPTY_EXTENSIONS =
      new IExtension[] {};

  private static final IExtensionPoint[] EMPTY_EXT_POINTS =
      new IExtensionPoint[] {};

  private static final IConfigurationElement[] EMPTY_CONF_ELEMENTS =
      new IConfigurationElement[] {};

  private static final CppDefaultRegistry INSTANCE = new CppDefaultRegistry();

  static void init() {
    try {
      RegistryProviderFactory.setDefault(INSTANCE);
    } catch (CoreException e) {
      log.error("Failed to set the default registry provider", e);
    }
  }

  static void release() {
    RegistryProviderFactory.releaseDefault();
  }

  @Override
  public IExtensionRegistry getRegistry() {
    return INSTANCE;
  }

  @Override
  public void stop(Object arg0) throws IllegalArgumentException {
    // no op
  }

  @Override
  public void removeRegistryChangeListener(IRegistryChangeListener arg0) {
    // no op
  }

  @Override
  public void removeListener(IRegistryEventListener arg0) {
    // no op
  }

  @Override
  public boolean removeExtensionPoint(IExtensionPoint arg0, Object arg1) {
    return false;
  }

  @Override
  public boolean removeExtension(IExtension arg0, Object arg1) {
    return false;
  }

  @Override
  public boolean isMultiLanguage() {
    return false;
  }

  @Override
  public String[] getNamespaces() {
    return EMPTY_NAMESPACES;
  }

  @Override
  public IExtension[] getExtensions(IContributor arg0) {
    return EMPTY_EXTENSIONS;
  }

  @Override
  public IExtension[] getExtensions(String arg0) {
    return EMPTY_EXTENSIONS;
  }

  @Override
  public IExtensionPoint[] getExtensionPoints(IContributor arg0) {
    return EMPTY_EXT_POINTS;
  }

  @Override
  public IExtensionPoint[] getExtensionPoints(String arg0) {
    return EMPTY_EXT_POINTS;
  }

  @Override
  public IExtensionPoint[] getExtensionPoints() {
    return EMPTY_EXT_POINTS;
  }

  @Override
  public IExtensionPoint getExtensionPoint(String arg0, String arg1) {
    return null;
  }

  @Override
  public IExtensionPoint getExtensionPoint(String arg0) {
    return null;
  }

  @Override
  public IExtension getExtension(String arg0, String arg1, String arg2) {
    return null;
  }

  @Override
  public IExtension getExtension(String arg0, String arg1) {
    return null;
  }

  @Override
  public IExtension getExtension(String arg0) {
    return null;
  }

  @Override
  public IConfigurationElement[] getConfigurationElementsFor(String arg0,
      String arg1, String arg2) {
    return EMPTY_CONF_ELEMENTS;
  }

  @Override
  public IConfigurationElement[] getConfigurationElementsFor(String arg0,
      String arg1) {
    return EMPTY_CONF_ELEMENTS;
  }

  @Override
  public IConfigurationElement[] getConfigurationElementsFor(String arg0) {
    return EMPTY_CONF_ELEMENTS;
  }

  @Override
  public void addRegistryChangeListener(IRegistryChangeListener arg0,
      String arg1) {
    // no op
  }

  @Override
  public void addRegistryChangeListener(IRegistryChangeListener arg0) {
    // no op
  }

  @Override
  public void addListener(IRegistryEventListener arg0, String arg1) {
    // no op
  }

  @Override
  public void addListener(IRegistryEventListener arg0) {
    // no op
  }

  @Override
  public boolean addContribution(InputStream arg0, IContributor arg1,
      boolean arg2, String arg3, ResourceBundle arg4, Object arg5) {
    return false;
  }
}
