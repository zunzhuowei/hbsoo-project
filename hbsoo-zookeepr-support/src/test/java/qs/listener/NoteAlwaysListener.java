package qs.listener;

import com.hbsoo.zoo.Zookit;
import com.hbsoo.zoo.model.NoteData;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created by zun.wei on 2019/5/23.
 */
@Component
public class NoteAlwaysListener {

    @Resource
    private Zookit zookit;

    @PostConstruct
    public void listen() {
        new Thread(() -> {
            String nodePath = "/listen";
            try {
                // 监听的是 path 节点 为：/workspace/listen
                zookit.listenNoteAlways(nodePath, () ->{
                    NoteData noteData = zookit.getNoteDataByNote(nodePath);
                    System.out.println("noteData = " + noteData);
                });

                // 监听的是 path 节点（/workspace/listen/）下的子节点
                zookit.listenNoteChildListAlways(nodePath, (client, event) -> {
                    switch (event.getType()) {
                        case INITIALIZED:
                            this.printlnHelp(event, "INITIALIZED");
                            break;
                        case CHILD_ADDED:
                            this.printlnHelp(event, "CHILD_ADDED");
                            break;
                        case CHILD_UPDATED:
                            this.printlnHelp(event, "CHILD_UPDATED");
                            break;
                        case CHILD_REMOVED:
                            this.printlnHelp(event, "CHILD_REMOVED");
                            break;
                        case CONNECTION_SUSPENDED:
                            this.printlnHelp(event, "CONNECTION_SUSPENDED");
                            break;
                        case CONNECTION_RECONNECTED:
                            this.printlnHelp(event, "CONNECTION_RECONNECTED");
                            break;
                        case CONNECTION_LOST:
                            this.printlnHelp(event, "CONNECTION_LOST");
                            break;
                        default:
                            throw new IllegalArgumentException("not type of --::" + event.getType());
                    }
                });

                zookit.listenNoteTreeAlways(nodePath, (client, event) -> {
                    switch (event.getType()) {
                        case NODE_ADDED:
                            this.printlnHelp(event, "NODE_ADDED");
                            break;
                        case NODE_UPDATED:
                            this.printlnHelp(event, "NODE_UPDATED");
                            break;
                        case NODE_REMOVED:
                            this.printlnHelp(event, "NODE_REMOVED");
                            break;
                        case INITIALIZED:
                            this.printlnHelp(event, "INITIALIZED");
                            break;
                        case CONNECTION_SUSPENDED:
                            this.printlnHelp(event, "CONNECTION_SUSPENDED");
                            break;
                        case CONNECTION_RECONNECTED:
                            this.printlnHelp(event, "CONNECTION_RECONNECTED");
                            break;
                        case CONNECTION_LOST:
                            this.printlnHelp(event, "CONNECTION_LOST");
                            break;
                        default:
                            throw new IllegalArgumentException("not type of --::" + event.getType());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void printlnHelp(PathChildrenCacheEvent event, String env) {
        ChildData childData = event.getData();
        if (Objects.nonNull(childData)) {
            String path = childData.getPath();
            String data = new String(childData.getData(), StandardCharsets.UTF_8);
            System.out.println(env + ": path,data = " + path + "," + data);
        }
    }

    private void printlnHelp(TreeCacheEvent event, String env) {
        ChildData childData = event.getData();
        if (Objects.nonNull(childData)) {
            String path = childData.getPath();
            String data = new String(childData.getData(), StandardCharsets.UTF_8);
            System.out.println(env + ": path,data = " + path + "," + data);
        }
    }

}
