package com.jeecms.main;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetty内置服务类
 */
public class Startup {

    private static final Logger log = LoggerFactory.getLogger(Startup.class);

    /**
     * 程序启动入口
     */
    public static void main(String[] args) {
        try {
            final String IPLATFORM_ROOT = System.getenv("JEECMS_HOME") != null ? System.getenv("JEECMS_HOME") : ".";
            final String RESOURCE_BASE = IPLATFORM_ROOT + File.separator + "WebContent";
            final String CONTEXT_PATH = "/jeecms";
            // -Dport=8080
            int port = Integer.getInteger("port", 8080).intValue();
            System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");
            Server server = new Server(port);
            server.setStopAtShutdown(true);
            WebAppContext context = new WebAppContext();

            context.setDisplayName("jeecms");
            context.setContextPath(CONTEXT_PATH);
            context.setDescriptor(RESOURCE_BASE + "/WEB-INF/web.xml");
            context.setResourceBase(RESOURCE_BASE);
            context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
            context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

            List<Resource> resources = new ArrayList<Resource>();

            // web content
            resources.add(Resource.newResource(RESOURCE_BASE));

            // class path
            final Resource classResource = Resource.newClassPathResource(".");
            if (classResource != null) {
                resources.add(classResource);
            }

            // in jar
            final URI uri = Startup.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            resources.add(Resource.newResource("jar:" + uri + "!/"));

            // module resource
            File module = new File(IPLATFORM_ROOT);
            if (module.exists()) {
                List<String> webappdirs = new ArrayList();
                findWebApp(module, webappdirs);
                for (String webappdir : webappdirs) {
                    resources.add(Resource.newResource(webappdir));
                }
            } else {
                log.info("not found module!");
                log.info("SYSTEM EXIT!");
                System.exit(0);
            }
            Resource[] collection = new Resource[resources.size()];
            int index = 0;
            for (Resource resource : resources) {
                log.info("resource:" + classResource);
                collection[index++] = resource;
            }
            context.setBaseResource(new ResourceCollection(collection));

            context.setConfigurationDiscovered(true);
            context.setParentLoaderPriority(true);
            server.setHandler(context);
            server.start();
            log.info("server is started");
            server.join();
        } catch (Exception e) {
            log.error("", e);
            System.exit(100);
        }
    }

    private static void findWebApp(File root, List<String> webappdirs) {
        if (root.exists() && root.isDirectory()) {
            for (File file : root.listFiles()) {
                if (file.isDirectory() && file.getName().equalsIgnoreCase("webapp")) {
                    webappdirs.add(file.getPath());
                } else if (file.isDirectory()) {
                    findWebApp(file, webappdirs);
                }
            }
        }
    }
}