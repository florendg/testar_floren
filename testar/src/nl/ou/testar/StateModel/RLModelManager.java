package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.apache.commons.lang.Validate;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Implementation of the {@link StateModelManager} for use of Sarsa.
 * Sarsa is a reinforcement learning (Artificial Intelligence) algorithm
 * for (sequential) action selection.
 */
public class RLModelManager extends ModelManager implements StateModelManager {

    protected static final Logger logger = LoggerFactory.getLogger(RLModelManager.class);

    /** The previously executed {@link AbstractAction} */
    protected AbstractAction previousAbstractActionToExecute = null;

    /**  The {@Link RewardFunction} determines the reward or penalty for executing an {@link AbstractAction}
    *  The reward is used in the {@link QFunction}
    */
    protected final RewardFunction rewardFunction;

    /**
     * The {@link QFunction} or Quality function determines the desirability of an {@link AbstractAction}
     */
    protected final QFunction qFunction;

    protected State state = null;

    protected final Tag<?> tag;

    /**
     * Constructor
     *
     */
    public RLModelManager(final AbstractStateModel abstractStateModel,
                             final ActionSelector actionSelector,
                             final PersistenceManager persistenceManager,
                             final Set<Tag<?>> concreteStateTags,
                             final SequenceManager sequenceManager,
                             final boolean storeWidgets,
                             final RewardFunction rewardFunction,
                             final QFunction qFunction,
                             final Tag<?> tag) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets);
        this.rewardFunction = rewardFunction;
        this.qFunction = qFunction;
        this.tag = tag;
    }

    @Override
    public void notifyNewStateReached(final State newState, final Set<Action> actions) {
        super.notifyNewStateReached(newState, actions);
        state = newState;

    }

    /**
     * Gets an {@link Action} to execute and updates the Q-value of the previously executed {@link Action}
     */
    @Override
    public Action getAbstractActionToExecute(final Set<Action> actions) {
        logger.info("Number of actions available:{}", actions.size());
        final Action selectedAction = super.getAbstractActionToExecute(actions);
        logger.info("Action selected:{}", selectedAction == null ? null :selectedAction.toShortString());
        return selectedAction;
    }

    /**
     * Update the Q-value for an {@link Action}
     *
     * @param selectedAbstractAction, can be null
     */
    protected void updateQValue(final AbstractAction selectedAbstractAction, final Set<Action> actions) {
        // get reward and Q-value
        float reward = rewardFunction.getReward(state, getCurrentConcreteState(), currentAbstractState, selectedAbstractAction, actions);
        System.out.println("REWARD: " + Float.toString(reward));
        final float qValue = qFunction.getQValue((Tag<Float>)this.tag, previousAbstractActionToExecute, selectedAbstractAction, reward, currentAbstractState, actions);

        // set attribute for saving in the graph database
        previousAbstractActionToExecute.addAttribute(tag, qValue);

    }

    @Override
    public void notifyTestSequenceStopped() {
        super.notifyTestSequenceStopped();
        rewardFunction.reset();
    }
}