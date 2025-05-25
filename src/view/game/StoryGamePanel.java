package view.game;

import controller.GameController;
import model.MapModel;
import view.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StoryGamePanel extends AbstractGamePanel {
    private boolean isStoryCompleted = false;
    private int levelIndex;
    private String STORY_TEXT;
    private Language currentLanguage = Language.CHINESE; // 默认中文
    private static final String[] STORY_TEXTS_CN = {
            "第一回 在这个混乱的时代，曹操和孙权的势力激烈对抗，刘备在夹缝中寻找生存之道...",
            "第二回 刘备为了寻求盟友，决定拜访诸葛亮，请他出山共图大业。",
            "第三回 诸葛亮三次被邀请，终于答应出山辅佐刘备，开启三顾茅庐的传奇。",
            "第四回 赤壁火攻撕裂曹军防线，东南风骤起却助长火势，孙刘联军反攻倒算。",
            "第五回 华容道浓雾锁路，关羽横刀立马，曹操残部困兽犹斗。",
            "第六回 周瑜假意联刘抗曹，暗中派兵夺取荆州，刘备被迫南征。",
            "第七回 曹操铁索连舟欲破江防，诸葛亮借东风火烧连船，曹营哀嚎震天。",
            "第八回 三顾茅庐终得军师，隆中对策定鼎三分天下格局。",
            "第九回 赵云单骑救主，银枪贯阵冲出曹军重围，怀中阿斗啼哭不止。",
            "第十回 诸葛亮舌战群儒，江东谋士哑口无言，孙权含恨联蜀抗曹。",
            "第十一回 草船借箭遇江雾弥漫，鲁肃暗叹诸葛神机，曹操气急败坏。",
            "第十二回 黄盖诈降身绑火油，乘东南风突入曹营，战船尽付祝融。",
            "第十三回 关羽义释曹操，恩怨两清却埋下荆州隐患。",
            "第十四回 周瑜箭伤复发咳血，鲁肃接掌东吴，荆州争夺暗流涌动。",
            "第十五回 司马懿空城抚琴退兵，诸葛亮汗透重衫，李严运粮误事埋祸根。",
            "第十六回 关羽水淹七军擒于禁，庞德抬棺死战，威震华夏却遭东吴暗算。",
            "第十七回 白帝城刘备托孤，诸葛亮涕零受命，汉室气数已近黄昏。",
            "第十八回 木牛流马运粮入川，蜀军粮道畅通，魏将张郃截粮遭伏击。",
            "第十九回 诸葛亮五丈原禳星失败，秋风卷走七星灯，北伐大业终成泡影。",
            "第二十回 姜维九伐中原耗尽国力，宦官黄皓弄权，蜀汉内部渐生裂痕。",
            "第二十一回 邓艾偷渡阴平，诸葛瞻犹豫不决，绵竹关文鸯单骑破敌阵。",
            "第二十二回 钟会韩遂密谋反叛，姜维假意投诚，剑阁道上演三国最后一战。",
            "第二十三回 司马昭弑君篡位，成济一戟弑君，洛阳满城风雨。",
            "第二十四回 邓艾钟会争功，卫瓘献计斩邓艾，钟会兵变终遭镇压。",
            "第二十五回 姜维诈降钟会，挑动魏军内斗，事败身死九伐功亏一篑。",
            "第二十六回 司马炎逼魏元帝禅让，陈留王泣血捧玺，西晋王朝正式建立。",
            "第二十七回 羊祜屯田蚕食荆州，陆抗筑城固守，两国边境暗藏刀兵。",
            "第二十八回 王濬楼船顺流而下，孙皓自缚请降，三国归晋终成定局。",
            "第二十九回 司马衷愚钝引发八王之乱，匈奴刘渊趁机起兵，永嘉之乱爆发。",
            "第三十回 杜预偷渡长江，王浑迟疑不进，灭吴之战险些功败垂成。",
            "第三十一回 贾充毒杀魏帝曹髦，成济代罪被诛，司马氏清洗宗室。",
            "第三十二回 诸葛瞻宠信黄皓，宦官专权误国，刘禅乐不思蜀埋祸根。",
            "第三十三回 姜维布阵沓中练兵，邓艾识破疑兵计，阴平小道成生死关。",
            "第三十四回 邓艾奇兵突袭江油，马邈不战而降，李严运粮再出纰漏。",
            "第三十五回 诸葛瞻临阵换将，董厥樊建丧失战机，绵竹关血流成河。",
            "第三十六回 司马炎统一后沉迷酒色，石崇王恺斗富，西晋奢靡之风盛行。",
            "第三十七回 裴秀绘制禹贡地域图，马钧改良织绫机，科技发展难挽颓势。",
            "第三十八回 刘渊建汉赵称帝，石勒建后赵，永嘉之乱开启五胡乱华。",
            "第三十九回 司马睿南渡建东晋，王导王敦掌权，王与马共天下。",
            "第四十回 祖逖中流击楫北伐，遇刁协掣肘，收复中原半壁又失。",
            "第四十一回 桓温三次北伐，枋头兵败威名受损，九锡之礼埋篡位伏笔。",
            "第四十二回 谢安淝水破苻坚，小儿歌谣传唱：'风声鹤唳，草木皆兵。'",
            "第四十三回 刘裕气吞万里如虎，灭南燕收巴蜀，却因功高震主遭猜忌。",
            "第四十四回 司马道子专权乱政，孙恩起义爆发，东晋统治岌岌可危。",
            "第四十五回 桓玄废晋自立，刘裕京口举义，桓楚政权三月而亡。",
            "第四十六回 刘裕北伐后秦，朱龄石奇袭长安，关中得而复失。",
            "第四十七回 司马德宗被弑，刘裕代晋建宋，寒门与士族矛盾激化。",
            "第四十八回 拓跋珪复国称代王，崔浩国史案牵连，北魏汉化进程受阻。",
            "第四十九回 沮渠蒙逊建北凉，昙无谶译经弘法，河西走廊佛教兴盛。",
            "第五十回 刘义隆元嘉北伐，檀道济唱筹量沙，费尽粮草终成笑谈。",
            "第五十一回 萧道成逼宋顺帝禅位，淮阴王藏入佛寺，南齐取代刘宋。",
            "第五十二回 萧衍舍身入寺，侯景之乱饿殍遍野，建康城沦为人间地狱。",
            "第五十三回 陈霸先偷袭石头城，侯景被部下所杀，南朝四代终成陈迹。"
    };

    private static final String[] STORY_TEXTS_EN = {
            "In this chaotic era, the forces of Cao Cao and Sun Quan clash fiercely, while Liu Bei seeks survival in between...",
            "Liu Bei, seeking allies, decides to visit Zhuge Liang and invite him to join his cause.",
            "Zhuge Liang, after being invited three times, finally agrees to assist Liu Bei — a tale of the famed Three Visits.",
            "Red Cliff fire shattered Cao's forces, sudden easterly wind fueled flames, Sun-Liu alliance counterattacked.",
            "Huarong Path shrouded in fog, Guan Yu blocked retreat with blade, Cao's remnants struggled.",
            "Zhou Yu pretended alliance with Shu, secretly seized Jingzhou, Liu Bei forced southward.",
            "Cao chained ships for stability, Zhuge Liang borrowed eastern wind to burn fleet, Cao's camp wailed.",
            "Three visits to thatched cottage finally secured Zhuge Liang, Longzhong Plan shaped tripartite balance.",
            "Zhao Yun charged through Wei lines with infant Liu Shan, silver spear carved path to safety.",
            "Zhuge Liang debated Jiangdong scholars into silence, Sun Quan begrudgingly allied with Shu.",
            "Grass boats 'borrowed' arrows in thick fog, Cao fumed while Lu Su marveled at Zhuge's wit.",
            "Huang Gai self-immolated on fire ships, broke through Cao's defenses during gale.",
            "Guan Yu spared Cao Cao at Huarong, settled old scores but sowed future discord.",
            "Zhou Yu's arrow wound worsened, Lu Su took command, Jingzhou rivalry intensified.",
            "Sima Yi used empty fort trick, Zhuge Liang played lute under pressure, Liang's grain blunder festered.",
            "Guan Yu flooded seven camps capturing Yu Jin, Pang De died in coffin, glory met betrayal.",
            "Liu Bei's deathbed edict moved Zhuge Liang to tears, Han's twilight deepened.",
            "Wooden oxen ensured Shu's grain supply, Zhang He intercepted but fell into ambush.",
            "Zhuge Liang's seven-star ritual failed at Wuzhang, northern campaigns died with him.",
            "Jiang Wei's nine invasions drained Shu, Huang Hao manipulated court, cracks emerged.",
            "Deng Ai sneaked through Yinping, Jiangyou surrendered, Wen Yin's lone charge saved Mianzhu.",
            "Zhong Hui schemed with Deng Ai, Jiang Wei faked surrender, Jiange became final battleground.",
            "Sima Zhao murdered Emperor, Cheng Ji speared royal, Luoyang plunged into chaos.",
            "Wei Guan plotted Deng Ai's demise, Zhong Hui's rebellion crushed, last hope expired.",
            "Jiang Wei deceived Zhong Hui till death, failed to revive Shu through palace intrigue.",
            "Sima Yan forced abdication, Chenliu King wept surrendering seal, Jin dynasty began.",
            "Yang Hu farmed Jingzhou, Lu Xun fortified Xiangyang, uneasy truce held.",
            "Wang Jun's ships crushed Jianye, Sun Hao surrendered bound, tripartite era ended.",
            "Sima Zhong's incompetence sparked Eight Princes' War, Liu Yuan founded Han Zhao.",
            "Wang Jun bypassed Yangtze, Wang Hun hesitated, Wu's fall narrowly achieved.",
            "Jia Chong poisoned Emperor, Cheng Ji scapegoated, Sima clan purged rivals.",
            "Zhuge Zhan coddled Huang Hao, eunuchs ruined Shu, Liu Shan surrendered passively.",
            "Jiang Wei drilled troops in Taxian, Deng Ai saw through feints, Jieting became deathtrap.",
            "Deng Ai took Mianzhu by surprise, Ma Miao surrendered, Liang's grain blunder repeated.",
            "Zhuge Zhan replaced generals, Dong Hui-Fan lost chance, Mianzhu drowned in blood.",
            "Sima Yan drowned in wine after unification, Shi Chong-Xi Wang flaunted wealth.",
            "Pei Xiu mapped China, Ma Jun improved looms, tech advanced but dynasty decayed.",
            "Liu Yuan claimed Han Zhao throne, Shi Le founded Later Zhao, Five Barbarians began.",
            "Sima Rui fled south, Wang Dao-Wang Dun shared power, 'Wang and Ma rule together'.",
            "Zu Ti fought north with oar song, Dou Liang hindered, half-recovered lands lost.",
            "Huan Wen's three northern expeditions, Fangqiu's sand trick, nine-tin reward foreshadowed.",
            "Xie An's Feishui triumph, 'wind-snakes and grass-soldiers' became legend.",
            "Liu Yu conquered south-north, envious courtiers plotted, Liu-Song replaced Jin.",
            "Sima Daozi misruled, Sun En rebellion erupted, Eastern Jin teetered.",
            "Huan Xuan usurped throne, Liu Yu rebelled at Jingkou, Chu regime collapsed in 3 months.",
            "Liu Yu's Later Qin campaign, Zhu Liang's sand trick, Chang'an reclaimed then lost.",
            "Sima Dewen murdered, Liu Yu founded Song, gentry-soldier rift intensified.",
            "Tuoba Gui revived Dai state, Cui Hao's history case hindered Hanization.",
            "Juqu Mengxun built Northern Liang, Kumarajiva translated sutras, Buddhism thrived.",
            "Liu Yilong's Menglianggu ruse, Tan Daoji's sand counting, recovered lands lost again.",
            "Xiao Daocheng forced Emperor Gong to abdicate, Prince of Huaihuai hid in temple.",
            "Xiao Yan neglected temple duties, Hou Jing rebellion starved Jiankang to death.",
            "Chen Baxian stormed Stone City, Hou Jing killed by own men, Southern Dynasties ended."
    };

    public StoryGamePanel(MapModel model, int levelIndex) {
        super(model);
        this.levelIndex = levelIndex;
        STORY_TEXT = getStoryTextByLanguage(this.currentLanguage);
    }

    @Override
    public void initialGame() {
        // 不立即调用 super.initialGame()，避免提前开始计时
        showStory();
    }

    @Override
    public void setController(GameController controller) {
        super.setController(controller);

        // 清除特效模式可能残留的状态
        controller.setMirrorMode(false);
        controller.setSlowMode(false);

        // 如果可能存在禁用的 box，也可清空（可选）
        for (Component comp : boardPanel.getComponents()) {
            if (comp instanceof BoxComponent box) {
                box.setDisabled(false);
            }
        }
    }

    private void showStory() {
        // 设置状态面板为垂直居中布局，并设置边距
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.removeAll();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // 添加边距

        JTextPane storyPane = createStyledTextPane("", "楷体", Font.BOLD, 14, Color.WHITE);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(storyPane);
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();

        // 打字效果
        Timer storyTimer = new Timer(100, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < STORY_TEXT.length()) {
                    String currentText = STORY_TEXT.substring(0, index + 1);
                    storyPane.setText(currentText);
                    index++;
                } else {
                    ((Timer) e.getSource()).stop();

                    // 剧情完成后延迟2秒开始游戏
                    Timer stopTimer = new Timer(2000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            switchToGamePanel();
                        }
                    });
                    stopTimer.setRepeats(false);
                    stopTimer.start();
                }
            }
        });
        storyTimer.start();
    }

    @Override
    public void updateLanguageTexts(Language currentLanguage) {
        this.currentLanguage = currentLanguage;
        updateCommonLabels(currentLanguage);
        // 根据语言设置剧情文本
        STORY_TEXT = getStoryTextByLanguage(currentLanguage);

        // 如果状态面板当前显示剧情，则更新剧情文本
        if (!isStoryCompleted) {
            showStoryTextWithTypingEffect();
            switchToGamePanel();
        }
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "时间: " : "Time: ";

        // 更新步数标签、时间标签的文本
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
        // 其他可能存在的文本也要更新...
    }

    private String getStoryTextByLanguage(Language language) {
        int index = Math.max(0, Math.min(levelIndex, STORY_TEXTS_CN.length - 1));
        return (language == Language.ENGLISH) ? STORY_TEXTS_EN[2] : STORY_TEXTS_CN[index];
    }

    private void showStoryTextWithTypingEffect() {
        statusPanel.removeAll();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // 添加边距

        JTextPane storyPane = createStyledTextPane("", "楷体", Font.BOLD, 14, Color.WHITE);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(storyPane);
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();

        Timer storyTimer = new Timer(100, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < STORY_TEXT.length()) {
                    String currentText = STORY_TEXT.substring(0, index + 1);
                    storyPane.setText(currentText);
                    index++;
                } else {
                    ((Timer) e.getSource()).stop();

                    Timer stopTimer = new Timer(2000, e2 -> switchToGamePanel());
                    stopTimer.setRepeats(false);
                    stopTimer.start();
                }
            }
        });
        storyTimer.start();
    }

    private JTextPane createStyledTextPane(String text, String fontName, int fontStyle, int fontSize, Color color) {
        JTextPane textPane = new JTextPane();
        textPane.setText(text);
        textPane.setFont(new Font(fontName, fontStyle, fontSize));
        textPane.setForeground(color);
        textPane.setOpaque(false);
        textPane.setEditable(false);
        textPane.setFocusable(false);
        textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // 内边距
        textPane.setPreferredSize(new Dimension(400, 130));  // 宽度可调节，影响换行
        return textPane;
    }

    private void switchToGamePanel() {
        statusPanel.removeAll();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // 保持边距
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        // 设置标签居中
        stepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        getTimeLabel().setAlignmentX(Component.CENTER_ALIGNMENT);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(stepLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(getTimeLabel());
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();

        // 现在才真正初始化游戏和启动计时器
        super.initialGame();
        super.setController(this.controller);

        isStoryCompleted = true;
    }

    public void setModeLevels(int[][][] modeLevels, int currentLevelIndex) {
        // 在关卡开始时设置地图和索引
        if (currentLevelIndex >= 0 && currentLevelIndex < modeLevels.length) {
            model.setMatrix(modeLevels[currentLevelIndex]);
        }
    }
}
