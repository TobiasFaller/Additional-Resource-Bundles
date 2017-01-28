# Additional Resource Bundles
This package provides two additional resource bundles (`java.util.ResourceBundle`) for application / testing purposes.

## Dynamic Resource Bundle
This resource bundle can be used for testing purposes and enables adding / removing of key value pairs.
The class is a simple wrapper for a `java.util.Map`.

```
DynamicResourceBundle bundle = new DynamicResourceBundle();
bundle.add("key", "value");
bundle.add("another key", "another value");
bundle.remove("key");
```

## Prefixed Resource Bundle
This resource bundle organizes it's sub bundles into namespaces (titled as prefix).
If no namespace (prefix) is defined the bundle is assigned into the default namespace without a prefix.
Subnamespaces override the entries of the parent namespaces.
A namespace can be shared by multiple resource bundles but the order in which the keys get traversed is
unspecified.

```
// Create data resource bundles
DynamicResourceBundle b0 = new DynamicResourceBundle();
b0.add("title", "My Application v1.0");

DynamicReourceBundle b1 = new DynamicResourceBundle();
b1.add("file", "File");
b1.add("lang", "Language");

DynamicResourceBundle b2 = new DynamicResourceBundle();
b2.add("new", "New");
b2.add("save_as", "Save as ...");

DynamicReourceBundle b3 = new DynamicResourceBundle();
b3.add("deu", "German");
b3.add("eng", "English");

// Create a combined resource bundle
PrefixedResourceBundle bundle = new PrefixedResourceBundle();
bundle.addDefaultBundle(b0);
bundle.addBundle("menu", b1);
bundle.addBundle("menu.file", b2);
bundle.addBundle("menu.lang", b3);

// The bundle contains the following entries:
/*
 * "title" -> "My Application v1.0"
 * "menu.file" -> "File"
 * "menu.file.new" -> "New"
 * "menu.file.save_as" -> "Save as ..."
 * "menu.lang" -> "Language"
 * "menu.lang.deu" -> "German"
 * "menu.lang.eng" -> "English"
 */
```