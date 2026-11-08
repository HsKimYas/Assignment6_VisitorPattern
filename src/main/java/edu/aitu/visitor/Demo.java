package edu.aitu.visitor;

import java.util.List;

/** Deterministic sample; optional arguments are a filename suffix and minimum bytes. */
public final class Demo {
    private Demo() {
    }

    public static void main(String[] args) {
        if (args.length > 2) {
            throw new IllegalArgumentException("Usage: Demo [filename-suffix] [minimum-bytes]");
        }
        String suffix = args.length >= 1 ? args[0] : ".java";
        long minimumSize = args.length == 2 ? Long.parseLong(args[1]) : 800;
        DirectoryNode root = createSampleTree();
        TotalSizeVisitor totalSize = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(suffix), minimumSize);
        StatisticsVisitor statistics = new StatisticsVisitor();
        FileSystemWalker.walk(root, totalSize);
        FileSystemWalker.walk(root, search);
        FileSystemWalker.walk(root, statistics);

        System.out.println("Visitor Pattern - In-Memory File System");
        System.out.println("Root: " + root.getName());
        System.out.println("Total size: " + totalSize.getTotalSize() + " bytes");
        System.out.println("Search: filename ends with \"" + suffix
                + "\", size >= " + minimumSize + " bytes");
        printMatches(search.getMatches());
        printStatistics(statistics.getStatistics());

        FileSystemWalker.walk(root, totalSize);
        System.out.println("Repeated total size: " + totalSize.getTotalSize() + " bytes");
    }

    private static void printStatistics(Statistics statistics) {
        System.out.println("Statistics: files=" + statistics.fileCount()
                + ", directories=" + statistics.directoryCount()
                + ", maximumDepth=" + statistics.maxDepth());
    }

    private static void printMatches(List<String> matches) {
        System.out.println("Matches (" + matches.size() + "):");
        for (String path : matches) {
            System.out.println("  " + path);
        }
    }

    /** Nine files (8500 bytes), seven directories and maximum edge depth three. */
    public static DirectoryNode createSampleTree() {
        DirectoryNode root = new DirectoryNode("workspace");
        root.addChild(new FileNode("README.md", 1200));
        root.addChild(new FileNode("notes.txt", 400));
        root.addChild(createSourceDirectory());
        root.addChild(createDocumentationDirectory());
        DirectoryNode assets = new DirectoryNode("assets");
        assets.addChild(new FileNode("logo.svg", 2400));
        root.addChild(assets);
        root.addChild(new FileNode("zero.bin", 0));
        return root;
    }

    private static DirectoryNode createSourceDirectory() {
        DirectoryNode source = new DirectoryNode("src");
        source.addChild(new FileNode("Main.java", 900));
        DirectoryNode model = new DirectoryNode("model");
        model.addChild(new FileNode("FileNode.java", 700));
        model.addChild(new FileNode("DirectoryNode.java", 1100));
        source.addChild(model);
        return source;
    }

    private static DirectoryNode createDocumentationDirectory() {
        DirectoryNode documentation = new DirectoryNode("docs");
        documentation.addChild(new FileNode("guide.md", 1500));
        DirectoryNode archive = new DirectoryNode("archive");
        archive.addChild(new FileNode("old-notes.txt", 300));
        documentation.addChild(archive);
        documentation.addChild(new DirectoryNode("empty"));
        return documentation;
    }
}
