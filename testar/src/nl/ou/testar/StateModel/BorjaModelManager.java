package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.Set;

public class BorjaModelManager extends RLModelManager implements StateModelManager {

    public BorjaModelManager(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager, Set<Tag<?>> concreteStateTags, SequenceManager sequenceManager, boolean storeWidgets, RewardFunction rewardFunction, QFunction qFunction, Tag<?> tag) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets, rewardFunction, qFunction, tag);
    }

    @Override
    public void notifyNewStateReached(final State newState, final Set<Action> actions) {
        super.notifyNewStateReached(newState, actions);
        state = newState;

        // Previous super invocation has created/updated currentAbstractState (+ AbstractActions)
        // If one AbstractAction of the currentAbstractState is new (has not a RLTag), set the default value to 1f
        for(AbstractAction abstractAction : currentAbstractState.getActions()) {
            if(abstractAction.getAttributes().get(tag, null) == null) {
                abstractAction.addAttribute(tag, 1f);
            }
        }
    }

    @Override
    public Action getAbstractActionToExecute(Set<Action> actions) {
        // First update Q, then select action
        updateQValue(null, actions);
        final Action selectedAction = super.getAbstractActionToExecute(actions);
        // set previousActionUnderExecute to current abstractActionToExecute for the next iteration
        try {
            previouslyExecutedAbstractAction = currentAbstractState.getAction(selectedAction.get(Tags.AbstractIDCustom, ""));
            previouslyExecutedTestarAction = selectedAction;
        }
        catch (ActionNotFoundException e){
            logger.debug("Update of previous action failed because: '{}'", e.getMessage());
        }

        return selectedAction;
    }

}
