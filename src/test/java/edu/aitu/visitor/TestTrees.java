package edu.aitu.visitor;

final class TestTrees {
    private TestTrees() {
    }

    static SampleTree sample() {
        DirectoryNode root = new DirectoryNode("coursework");
        DirectoryNode assets = new DirectoryNode("assets");
        DirectoryNode notes = new DirectoryNode("notes");
        DirectoryNode archive = new DirectoryNode("archive");

        root.addChild(new FileNode("readme.md", 300));
        root.addChild(notes);
        root.addChild(assets);
        root.addChild(new DirectoryNode("empty"));
        assets.addChild(new FileNode("banner.png", 1_400));
        assets.addChild(new FileNode("empty.bin", 0));
        notes.addChild(new FileNode("week1.txt", 120));
        notes.addChild(archive);
        archive.addChild(new FileNode("report.txt", 260));
        archive.addChild(new FileNode("draft.txt", 119));

        return new SampleTree(root, notes, archive);
    }

    record SampleTree(DirectoryNode root, DirectoryNode notes, DirectoryNode archive) {
    }
}
