package nl.ou.testar.StateModel.Persistence;

import nl.ou.testar.StateModel.*;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Exception.InvalidEventException;
import nl.ou.testar.StateModel.Sequence.Sequence;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceNode;
import nl.ou.testar.StateModel.Sequence.SequenceStep;
import nl.ou.testar.StateModel.Util.EventHelper;

import java.util.ArrayDeque;

public class QueueManager implements PersistenceManager, StateModelEventListener {

    /**
     * A queue holding the commands to execute
     */
    private ArrayDeque<Runnable> queue;

    /**
     * Composite persistencemanager that will do the actual work for us.
     */
    private PersistenceManager delegateManager;

    /**
     * Helper class for dealing with events
     */
    private EventHelper eventHelper;

    /**
     * Is the event listener processing events?
     */
    private boolean listening = true;

    /**
     * Is the queue manager running in hybrid mode?
     */
    private boolean hybridMode;

    public QueueManager(PersistenceManager persistenceManager, EventHelper eventHelper, boolean hybridMode) {
        delegateManager = persistenceManager;
        queue = new ArrayDeque<>();
        this.eventHelper = eventHelper;
        this.hybridMode = hybridMode;
    }

    private void processRequest(Runnable runnable, Persistable persistable) {
        if (!hybridMode || persistable.canBeDelayed()) {
            queue.add(runnable);
        }
        else {
            runnable.run();
        }
    }

    @Override
    public void persistAbstractStateModel(AbstractStateModel abstractStateModel) {
        while (!queue.isEmpty()) {
            queue.remove().run();
        }
    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {
        processRequest(() -> delegateManager.persistAbstractState(abstractState), abstractState);
    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {
        processRequest(() -> delegateManager.persistAbstractAction(abstractAction), abstractAction);
    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {
        processRequest(() -> delegateManager.persistAbstractStateTransition(abstractStateTransition), abstractStateTransition);
    }

    @Override
    public void persistConcreteState(ConcreteState concreteState) {
        processRequest(() -> delegateManager.persistConcreteState(concreteState), concreteState);
    }

    @Override
    public void persistConcreteStateTransition(ConcreteStateTransition concreteStateTransition) {
        processRequest(() -> delegateManager.persistConcreteStateTransition(concreteStateTransition), concreteStateTransition);
    }

    @Override
    public void initAbstractStateModel(AbstractStateModel abstractStateModel) {
        delegateManager.initAbstractStateModel(abstractStateModel);
    }

    @Override
    public void persistSequence(Sequence sequence) {
        processRequest(() -> delegateManager.persistSequence(sequence), sequence);
    }

    @Override
    public void initSequenceManager(SequenceManager sequenceManager) {
        delegateManager.initSequenceManager(sequenceManager);
    }

    @Override
    public void persistSequenceNode(SequenceNode sequenceNode) {
        processRequest(() -> delegateManager.persistSequenceNode(sequenceNode), sequenceNode);
    }

    @Override
    public void persistSequenceStep(SequenceStep sequenceStep) {
        processRequest(() -> delegateManager.persistSequenceStep(sequenceStep), sequenceStep);
    }

    @Override
    public void eventReceived(StateModelEvent event) {
        if (!listening) return;

        try {
            eventHelper.validateEvent(event);
        } catch (InvalidEventException e) {
            // There is something wrong with the event. we do nothing and exit
            return;
        }

        switch (event.getEventType()) {
            case ABSTRACT_STATE_ADDED:
            case ABSTRACT_STATE_CHANGED:
                persistAbstractState((AbstractState) (event.getPayload()));
                break;

            case ABSTRACT_STATE_TRANSITION_ADDED:
            case ABSTRACT_ACTION_CHANGED:
                persistAbstractStateTransition((AbstractStateTransition) (event.getPayload()));
                break;

            case ABSTRACT_STATE_MODEL_INITIALIZED:
                initAbstractStateModel((AbstractStateModel) (event.getPayload()));
                break;

            case SEQUENCE_STARTED:
                persistSequence((Sequence) event.getPayload());
                break;

            case SEQUENCE_MANAGER_INITIALIZED:
                initSequenceManager((SequenceManager) event.getPayload());
                break;

            case SEQUENCE_NODE_ADDED:
                persistSequenceNode((SequenceNode) event.getPayload());
                break;

            case SEQUENCE_STEP_ADDED:
                persistSequenceStep((SequenceStep) event.getPayload());
        }
    }

    @Override
    public void setListening(boolean listening) {
        this.listening = listening;
    }
}