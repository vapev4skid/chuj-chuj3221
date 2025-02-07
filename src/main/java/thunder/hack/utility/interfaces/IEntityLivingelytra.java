package thunder.hack.utility.interfaces;

import thunder.hack.features.modules.combat.Aura;
import thunder.hack.features.modules.combat.ElytraTarget;

import java.util.List;

public interface IEntityLivingelytra {
    double getPrevServerX();

    double getPrevServerY();

    double getPrevServerZ();

    List<ElytraTarget.Position> getPositionHistory();
}
