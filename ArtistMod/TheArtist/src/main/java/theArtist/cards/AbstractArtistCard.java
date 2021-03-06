package theArtist.cards;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import theArtist.AbstractCanvas;
import theArtist.ArtistMod;
import theArtist.TheArtist;
import theArtist.actions.PaintAction;

import java.util.ArrayList;
import java.util.List;

import static theArtist.ArtistMod.makeCardPath;
import static theArtist.ArtistMod.theCanvas;

public abstract class AbstractArtistCard extends CustomCard {

    protected final CardStrings cardStrings;
    protected final String NAME;
    protected final String DESCRIPTION;
    protected final String UPGRADE_DESCRIPTION;
    protected final String[] EXTENDED_DESCRIPTION;

    public AbstractArtistCard(final String id, final int cost, final CardType type, final CardRarity rarity, final CardTarget target) {
        super(id, "ERROR", makeCardPath(id.replaceAll("artistmod:", "")) + ".png", cost, "ERROR", type, TheArtist.Enums.COLOR_RAINBOW, rarity, target);
        cardStrings = CardCrawlGame.languagePack.getCardStrings(id);
        name = NAME = cardStrings.NAME;
        originalName = NAME;
        rawDescription = DESCRIPTION = cardStrings.DESCRIPTION;
        UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
        EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
        initializeTitle();
        initializeDescription();
    }

    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> tips = new ArrayList<>();
        if (this.rawDescription.toLowerCase().contains("red")) {
            tips.add(new TooltipInfo("Red", "Color. Deals damage."));
        }
        if (this.rawDescription.toLowerCase().contains("blue")) {
            tips.add(new TooltipInfo("Blue", "Color. Grants #yBlock."));
        }
        if (this.rawDescription.toLowerCase().contains("green")) {
            tips.add(new TooltipInfo("Green", "Color. Draws cards."));
        }
        if (this.rawDescription.toLowerCase().contains("yellow")) {
            tips.add(new TooltipInfo("Yellow", "Color. Grants [E] . Removed after the first use."));
        }
        if (this.rawDescription.toLowerCase().contains("aqua")) {
            tips.add(new TooltipInfo("Aqua", "Color. Applies #yWeak."));
        }
        if (this.rawDescription.toLowerCase().contains("magenta")) {
            tips.add(new TooltipInfo("Magenta", "Color. Applies #yVulnerable."));
        }
        if (this.rawDescription.toLowerCase().contains("pink")) {
            tips.add(new TooltipInfo("Pink", "Color. Grants #yTemporary #yHP."));
        }
        if (this.rawDescription.toLowerCase().contains("purple")) {
            tips.add(new TooltipInfo("Purple", "Color. Applies #yCursed."));
        }
        if (this.rawDescription.toLowerCase().contains("rainbow")) {
            tips.add(new TooltipInfo("Rainbow", "Color. #yPaints."));
        }
        if (this.rawDescription.toLowerCase().contains("darken")) {
            tips.add(new TooltipInfo("Darken", "Increase the cost and effectiveness of the #yPainting."));
        }
        return tips;
    }

    @Override
    public void update() {
        super.update();
        if (this instanceof AbstractPaintingCard) {
            glowColor = ArtistMod.rainbow.cpy();
        } else if ((this.rawDescription.contains("Chromatic") && theCanvas.chromatic()) || (this.rawDescription.contains("Clear") && theCanvas.clear())) {
            glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR;
        } else {
            glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR;
        }
    }

    public void loadPaintingCardImage(Texture tex) {
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);// 215
        int tw = tex.getWidth();// 216
        int th = tex.getHeight();// 217
        TextureAtlas.AtlasRegion cardImg = new TextureAtlas.AtlasRegion(tex, 0, 0, tw, th);// 218
        ReflectionHacks.setPrivateInherited(this, CustomCard.class, "portrait", cardImg);// 219
    }


    public static String makeID(String blah) {
        return "artistmod:" + blah;
    }

    public void atb(AbstractGameAction action) {
        addToBot(action);
    }

    public DamageInfo makeInfo() {
        return makeInfo(damageTypeForTurn);
    }

    public DamageInfo makeInfo(DamageInfo.DamageType type) {
        return new DamageInfo(AbstractDungeon.player, damage, type);
    }

    public DamageAction dmg(AbstractMonster m, DamageInfo info, AbstractGameAction.AttackEffect fx) {
        return new DamageAction(m, info, fx);
    }

    public DamageAllEnemiesAction allDmg(AbstractGameAction.AttackEffect fx) {
        return new DamageAllEnemiesAction(AbstractDungeon.player, multiDamage, damageTypeForTurn, fx);
    }

    public GainBlockAction blck() {
        return new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, block);
    }

    public MakeTempCardInHandAction makeInHand(AbstractCard c, int i) {
        return new MakeTempCardInHandAction(c, i);
    }

    public MakeTempCardInHandAction makeInHand(AbstractCard c) {
        return makeInHand(c, 1);
    }

    public MakeTempCardInDrawPileAction shuffleIn(AbstractCard c, int i) {
        return new MakeTempCardInDrawPileAction(c, i, false, true);
    }

    public MakeTempCardInDrawPileAction shuffleIn(AbstractCard c) {
        return shuffleIn(c, 1);
    }

    public PaintAction paint(AbstractCanvas.VexColor color) {
        return new PaintAction(color);
    }

    public PaintAction paint(AbstractCanvas.VexColor color, int amount) {
        return new PaintAction(color, amount);
    }

    public ArrayList<AbstractMonster> monsterList() {
        return AbstractDungeon.getMonsters().monsters;
    }

    ApplyPowerAction applyToEnemy(AbstractMonster m, AbstractPower po) {
        return new ApplyPowerAction(m, AbstractDungeon.player, po, po.amount);
    }

    ApplyPowerAction applyToSelf(AbstractPower po) {
        return new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, po, po.amount);
    }

    public static AbstractCanvas.VexColor getRandomColor() {
        ArrayList<AbstractCanvas.VexColor> blah = new ArrayList<>();
        blah.add(AbstractCanvas.VexColor.RED);
        blah.add(AbstractCanvas.VexColor.BLUE);
        blah.add(AbstractCanvas.VexColor.GREEN);
        blah.add(AbstractCanvas.VexColor.YELLOW);
        blah.add(AbstractCanvas.VexColor.AQUA);
        blah.add(AbstractCanvas.VexColor.MAGENTA);
        blah.add(AbstractCanvas.VexColor.RAINBOW);
        return blah.get(MathUtils.random(blah.size() - 1));
    }

    WeakPower autoWeak(AbstractMonster m, int i) {
        return new WeakPower(m, i, false);
    }

    VulnerablePower autoVuln(AbstractMonster m, int i) {
        return new VulnerablePower(m, i, false);
    }
}