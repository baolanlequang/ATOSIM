/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.palladiosimulator.pcm.core.entity.EntityPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelFactory
 * @model kind="package"
 * @generated
 */
public interface AttackmodelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "attackmodel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://palladiosimulator.org/BlockchainSystemComponentModel/AttackModel/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "attackmodel";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AttackmodelPackage eINSTANCE = org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl
			.init();

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl <em>Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttack()
	 * @generated
	 */
	int ATTACK = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK__TYPE = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK__REQUIRED_HASH_POWER = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK__DEPENDS_ON_NETWORK_DELAY = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK__BASELINE_SUCCESS_PROBABILITY = EntityPackage.ENTITY_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK__PARAMETERS = EntityPackage.ENTITY_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK__GAMMA = EntityPackage.ENTITY_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackScenarioImpl <em>Attack Scenario</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackScenarioImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttackScenario()
	 * @generated
	 */
	int ATTACK_SCENARIO = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_SCENARIO__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_SCENARIO__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Attack</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_SCENARIO__ATTACK = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Attackers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_SCENARIO__ATTACKERS = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Blockchain System</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Result</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_SCENARIO__RESULT = EntityPackage.ENTITY_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Attack Scenario</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_SCENARIO_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackerNodeImpl <em>Attacker Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackerNodeImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttackerNode()
	 * @generated
	 */
	int ATTACKER_NODE = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACKER_NODE__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACKER_NODE__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Power Share</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACKER_NODE__POWER_SHARE = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Linked Node System</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACKER_NODE__LINKED_NODE_SYSTEM = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Attacker Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACKER_NODE_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl <em>Attack Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttackResult()
	 * @generated
	 */
	int ATTACK_RESULT = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Success</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__SUCCESS = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__DURATION = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Affected Blocks</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__AFFECTED_BLOCKS = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Success Probability Observed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED = EntityPackage.ENTITY_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Avg Fork Rate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__AVG_FORK_RATE = EntityPackage.ENTITY_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Stale Block Rate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__STALE_BLOCK_RATE = EntityPackage.ENTITY_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Double Spend Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__DOUBLE_SPEND_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Time To Takeover</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__TIME_TO_TAKEOVER = EntityPackage.ENTITY_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Fork Depth Distribution</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION = EntityPackage.ENTITY_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Attacker Revenue Ratio</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT__ATTACKER_REVENUE_RATIO = EntityPackage.ENTITY_FEATURE_COUNT + 9;

	/**
	 * The number of structural features of the '<em>Attack Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_RESULT_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 10;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackParameterImpl <em>Attack Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackParameterImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttackParameter()
	 * @generated
	 */
	int ATTACK_PARAMETER = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_PARAMETER__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_PARAMETER__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_PARAMETER__KEY = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_PARAMETER__VALUE = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Attack Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACK_PARAMETER_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.ForkDepthEntryImpl <em>Fork Depth Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.ForkDepthEntryImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getForkDepthEntry()
	 * @generated
	 */
	int FORK_DEPTH_ENTRY = 5;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORK_DEPTH_ENTRY__ID = EntityPackage.ENTITY__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORK_DEPTH_ENTRY__ENTITY_NAME = EntityPackage.ENTITY__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Depth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORK_DEPTH_ENTRY__DEPTH = EntityPackage.ENTITY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORK_DEPTH_ENTRY__COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Fork Depth Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORK_DEPTH_ENTRY_FEATURE_COUNT = EntityPackage.ENTITY_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl <em>Race Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getRaceAttack()
	 * @generated
	 */
	int RACE_ATTACK = 6;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__ID = ATTACK__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__ENTITY_NAME = ATTACK__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__TYPE = ATTACK__TYPE;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__REQUIRED_HASH_POWER = ATTACK__REQUIRED_HASH_POWER;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__DEPENDS_ON_NETWORK_DELAY = ATTACK__DEPENDS_ON_NETWORK_DELAY;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__BASELINE_SUCCESS_PROBABILITY = ATTACK__BASELINE_SUCCESS_PROBABILITY;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__PARAMETERS = ATTACK__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__GAMMA = ATTACK__GAMMA;

	/**
	 * The feature id for the '<em><b>Propagation Advantage</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__PROPAGATION_ADVANTAGE = ATTACK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Transaction ADelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__TRANSACTION_ADELAY = ATTACK_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Transaction BAcceleration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__TRANSACTION_BACCELERATION = ATTACK_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Fee Difference Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD = ATTACK_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Zero Conf Acceptance Risk</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK = ATTACK_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Observed Double Spends</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__OBSERVED_DOUBLE_SPENDS = ATTACK_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Avg Propagation Adv Ms</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK__AVG_PROPAGATION_ADV_MS = ATTACK_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Race Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RACE_ATTACK_FEATURE_COUNT = ATTACK_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl <em>Finney Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getFinneyAttack()
	 * @generated
	 */
	int FINNEY_ATTACK = 7;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__ID = ATTACK__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__ENTITY_NAME = ATTACK__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__TYPE = ATTACK__TYPE;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__REQUIRED_HASH_POWER = ATTACK__REQUIRED_HASH_POWER;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__DEPENDS_ON_NETWORK_DELAY = ATTACK__DEPENDS_ON_NETWORK_DELAY;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__BASELINE_SUCCESS_PROBABILITY = ATTACK__BASELINE_SUCCESS_PROBABILITY;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__PARAMETERS = ATTACK__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__GAMMA = ATTACK__GAMMA;

	/**
	 * The feature id for the '<em><b>Number Of Premined Blocks</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS = ATTACK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Withhold Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__WITHHOLD_TIME = ATTACK_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Merchant Accepts Zero Conf</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF = ATTACK_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Double Spend Success Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT = ATTACK_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Observed Time To Release Ms</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS = ATTACK_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Finney Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINNEY_ATTACK_FEATURE_COUNT = ATTACK_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl <em>Selfish Mining Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getSelfishMiningAttack()
	 * @generated
	 */
	int SELFISH_MINING_ATTACK = 8;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__ID = ATTACK__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__ENTITY_NAME = ATTACK__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__TYPE = ATTACK__TYPE;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__REQUIRED_HASH_POWER = ATTACK__REQUIRED_HASH_POWER;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__DEPENDS_ON_NETWORK_DELAY = ATTACK__DEPENDS_ON_NETWORK_DELAY;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__BASELINE_SUCCESS_PROBABILITY = ATTACK__BASELINE_SUCCESS_PROBABILITY;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__PARAMETERS = ATTACK__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__GAMMA = ATTACK__GAMMA;

	/**
	 * The feature id for the '<em><b>Private Chain Lead Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET = ATTACK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Withhold Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY = ATTACK_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Publish On Catchup</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP = ATTACK_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Expected Forks Per Hour</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR = ATTACK_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Monitored Nodes</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK__MONITORED_NODES = ATTACK_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Selfish Mining Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELFISH_MINING_ATTACK_FEATURE_COUNT = ATTACK_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.StubbornMiningAttackImpl <em>Stubborn Mining Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.StubbornMiningAttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getStubbornMiningAttack()
	 * @generated
	 */
	int STUBBORN_MINING_ATTACK = 9;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__ID = ATTACK__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__ENTITY_NAME = ATTACK__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__TYPE = ATTACK__TYPE;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__REQUIRED_HASH_POWER = ATTACK__REQUIRED_HASH_POWER;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__DEPENDS_ON_NETWORK_DELAY = ATTACK__DEPENDS_ON_NETWORK_DELAY;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__BASELINE_SUCCESS_PROBABILITY = ATTACK__BASELINE_SUCCESS_PROBABILITY;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__PARAMETERS = ATTACK__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__GAMMA = ATTACK__GAMMA;

	/**
	 * The feature id for the '<em><b>Lead Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__LEAD_THRESHOLD = ATTACK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Abandon Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD = ATTACK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Stubborn Mining Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUBBORN_MINING_ATTACK_FEATURE_COUNT = ATTACK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.EqualForkStubbornAttackImpl <em>Equal Fork Stubborn Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.EqualForkStubbornAttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getEqualForkStubbornAttack()
	 * @generated
	 */
	int EQUAL_FORK_STUBBORN_ATTACK = 10;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__ID = STUBBORN_MINING_ATTACK__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__ENTITY_NAME = STUBBORN_MINING_ATTACK__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__TYPE = STUBBORN_MINING_ATTACK__TYPE;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__REQUIRED_HASH_POWER = STUBBORN_MINING_ATTACK__REQUIRED_HASH_POWER;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__DEPENDS_ON_NETWORK_DELAY = STUBBORN_MINING_ATTACK__DEPENDS_ON_NETWORK_DELAY;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__BASELINE_SUCCESS_PROBABILITY = STUBBORN_MINING_ATTACK__BASELINE_SUCCESS_PROBABILITY;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__PARAMETERS = STUBBORN_MINING_ATTACK__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__GAMMA = STUBBORN_MINING_ATTACK__GAMMA;

	/**
	 * The feature id for the '<em><b>Lead Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__LEAD_THRESHOLD = STUBBORN_MINING_ATTACK__LEAD_THRESHOLD;

	/**
	 * The feature id for the '<em><b>Abandon Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__ABANDON_THRESHOLD = STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD;

	/**
	 * The feature id for the '<em><b>Tie Resolution Bias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK__TIE_RESOLUTION_BIAS = STUBBORN_MINING_ATTACK_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Equal Fork Stubborn Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EQUAL_FORK_STUBBORN_ATTACK_FEATURE_COUNT = STUBBORN_MINING_ATTACK_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.LeadStubbornAttackImpl <em>Lead Stubborn Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.LeadStubbornAttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getLeadStubbornAttack()
	 * @generated
	 */
	int LEAD_STUBBORN_ATTACK = 11;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__ID = STUBBORN_MINING_ATTACK__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__ENTITY_NAME = STUBBORN_MINING_ATTACK__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__TYPE = STUBBORN_MINING_ATTACK__TYPE;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__REQUIRED_HASH_POWER = STUBBORN_MINING_ATTACK__REQUIRED_HASH_POWER;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__DEPENDS_ON_NETWORK_DELAY = STUBBORN_MINING_ATTACK__DEPENDS_ON_NETWORK_DELAY;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__BASELINE_SUCCESS_PROBABILITY = STUBBORN_MINING_ATTACK__BASELINE_SUCCESS_PROBABILITY;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__PARAMETERS = STUBBORN_MINING_ATTACK__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__GAMMA = STUBBORN_MINING_ATTACK__GAMMA;

	/**
	 * The feature id for the '<em><b>Lead Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__LEAD_THRESHOLD = STUBBORN_MINING_ATTACK__LEAD_THRESHOLD;

	/**
	 * The feature id for the '<em><b>Abandon Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__ABANDON_THRESHOLD = STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD;

	/**
	 * The feature id for the '<em><b>Min Lead To Publish</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK__MIN_LEAD_TO_PUBLISH = STUBBORN_MINING_ATTACK_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Lead Stubborn Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LEAD_STUBBORN_ATTACK_FEATURE_COUNT = STUBBORN_MINING_ATTACK_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.TrailStubbornAttackImpl <em>Trail Stubborn Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.TrailStubbornAttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getTrailStubbornAttack()
	 * @generated
	 */
	int TRAIL_STUBBORN_ATTACK = 12;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__ID = STUBBORN_MINING_ATTACK__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__ENTITY_NAME = STUBBORN_MINING_ATTACK__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__TYPE = STUBBORN_MINING_ATTACK__TYPE;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__REQUIRED_HASH_POWER = STUBBORN_MINING_ATTACK__REQUIRED_HASH_POWER;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__DEPENDS_ON_NETWORK_DELAY = STUBBORN_MINING_ATTACK__DEPENDS_ON_NETWORK_DELAY;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__BASELINE_SUCCESS_PROBABILITY = STUBBORN_MINING_ATTACK__BASELINE_SUCCESS_PROBABILITY;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__PARAMETERS = STUBBORN_MINING_ATTACK__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__GAMMA = STUBBORN_MINING_ATTACK__GAMMA;

	/**
	 * The feature id for the '<em><b>Lead Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__LEAD_THRESHOLD = STUBBORN_MINING_ATTACK__LEAD_THRESHOLD;

	/**
	 * The feature id for the '<em><b>Abandon Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__ABANDON_THRESHOLD = STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD;

	/**
	 * The feature id for the '<em><b>Continue On Trail</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK__CONTINUE_ON_TRAIL = STUBBORN_MINING_ATTACK_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Trail Stubborn Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRAIL_STUBBORN_ATTACK_FEATURE_COUNT = STUBBORN_MINING_ATTACK_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.MajorityAttackImpl <em>Majority Attack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.MajorityAttackImpl
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getMajorityAttack()
	 * @generated
	 */
	int MAJORITY_ATTACK = 13;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__ID = ATTACK__ID;

	/**
	 * The feature id for the '<em><b>Entity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__ENTITY_NAME = ATTACK__ENTITY_NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__TYPE = ATTACK__TYPE;

	/**
	 * The feature id for the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__REQUIRED_HASH_POWER = ATTACK__REQUIRED_HASH_POWER;

	/**
	 * The feature id for the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__DEPENDS_ON_NETWORK_DELAY = ATTACK__DEPENDS_ON_NETWORK_DELAY;

	/**
	 * The feature id for the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__BASELINE_SUCCESS_PROBABILITY = ATTACK__BASELINE_SUCCESS_PROBABILITY;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__PARAMETERS = ATTACK__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Gamma</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__GAMMA = ATTACK__GAMMA;

	/**
	 * The feature id for the '<em><b>Hash Power Share</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__HASH_POWER_SHARE = ATTACK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Expected Takeover Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__EXPECTED_TAKEOVER_TIME = ATTACK_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Target Confirmations To Break</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__TARGET_CONFIRMATIONS_TO_BREAK = ATTACK_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Colluding Pools</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK__COLLUDING_POOLS = ATTACK_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Majority Attack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAJORITY_ATTACK_FEATURE_COUNT = ATTACK_FEATURE_COUNT + 4;

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack <em>Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack
	 * @generated
	 */
	EClass getAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getType()
	 * @see #getAttack()
	 * @generated
	 */
	EAttribute getAttack_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getRequiredHashPower <em>Required Hash Power</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required Hash Power</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getRequiredHashPower()
	 * @see #getAttack()
	 * @generated
	 */
	EAttribute getAttack_RequiredHashPower();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#isDependsOnNetworkDelay <em>Depends On Network Delay</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Depends On Network Delay</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#isDependsOnNetworkDelay()
	 * @see #getAttack()
	 * @generated
	 */
	EAttribute getAttack_DependsOnNetworkDelay();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getBaselineSuccessProbability <em>Baseline Success Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Baseline Success Probability</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getBaselineSuccessProbability()
	 * @see #getAttack()
	 * @generated
	 */
	EAttribute getAttack_BaselineSuccessProbability();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getParameters()
	 * @see #getAttack()
	 * @generated
	 */
	EReference getAttack_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getGamma <em>Gamma</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gamma</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getGamma()
	 * @see #getAttack()
	 * @generated
	 */
	EAttribute getAttack_Gamma();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario <em>Attack Scenario</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attack Scenario</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario
	 * @generated
	 */
	EClass getAttackScenario();

	/**
	 * Returns the meta object for the containment reference '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getAttack <em>Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getAttack()
	 * @see #getAttackScenario()
	 * @generated
	 */
	EReference getAttackScenario_Attack();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getAttackers <em>Attackers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attackers</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getAttackers()
	 * @see #getAttackScenario()
	 * @generated
	 */
	EReference getAttackScenario_Attackers();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getBlockchainSystem <em>Blockchain System</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Blockchain System</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getBlockchainSystem()
	 * @see #getAttackScenario()
	 * @generated
	 */
	EReference getAttackScenario_BlockchainSystem();

	/**
	 * Returns the meta object for the containment reference '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Result</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getResult()
	 * @see #getAttackScenario()
	 * @generated
	 */
	EReference getAttackScenario_Result();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode <em>Attacker Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attacker Node</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode
	 * @generated
	 */
	EClass getAttackerNode();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode#getPowerShare <em>Power Share</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Power Share</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode#getPowerShare()
	 * @see #getAttackerNode()
	 * @generated
	 */
	EAttribute getAttackerNode_PowerShare();

	/**
	 * Returns the meta object for the reference '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode#getLinkedNodeSystem <em>Linked Node System</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Linked Node System</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode#getLinkedNodeSystem()
	 * @see #getAttackerNode()
	 * @generated
	 */
	EReference getAttackerNode_LinkedNodeSystem();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult <em>Attack Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attack Result</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult
	 * @generated
	 */
	EClass getAttackResult();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#isSuccess <em>Success</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Success</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#isSuccess()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_Success();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getDuration <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Duration</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getDuration()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_Duration();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAffectedBlocks <em>Affected Blocks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Affected Blocks</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAffectedBlocks()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_AffectedBlocks();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getSuccessProbabilityObserved <em>Success Probability Observed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Success Probability Observed</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getSuccessProbabilityObserved()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_SuccessProbabilityObserved();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAvgForkRate <em>Avg Fork Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Avg Fork Rate</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAvgForkRate()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_AvgForkRate();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getStaleBlockRate <em>Stale Block Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Stale Block Rate</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getStaleBlockRate()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_StaleBlockRate();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getDoubleSpendCount <em>Double Spend Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Double Spend Count</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getDoubleSpendCount()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_DoubleSpendCount();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getTimeToTakeover <em>Time To Takeover</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time To Takeover</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getTimeToTakeover()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_TimeToTakeover();

	/**
	 * Returns the meta object for the containment reference list '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getForkDepthDistribution <em>Fork Depth Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Fork Depth Distribution</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getForkDepthDistribution()
	 * @see #getAttackResult()
	 * @generated
	 */
	EReference getAttackResult_ForkDepthDistribution();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAttackerRevenueRatio <em>Attacker Revenue Ratio</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Attacker Revenue Ratio</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAttackerRevenueRatio()
	 * @see #getAttackResult()
	 * @generated
	 */
	EAttribute getAttackResult_AttackerRevenueRatio();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter <em>Attack Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attack Parameter</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter
	 * @generated
	 */
	EClass getAttackParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter#getKey()
	 * @see #getAttackParameter()
	 * @generated
	 */
	EAttribute getAttackParameter_Key();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter#getValue()
	 * @see #getAttackParameter()
	 * @generated
	 */
	EAttribute getAttackParameter_Value();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.ForkDepthEntry <em>Fork Depth Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Fork Depth Entry</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.ForkDepthEntry
	 * @generated
	 */
	EClass getForkDepthEntry();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.ForkDepthEntry#getDepth <em>Depth</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Depth</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.ForkDepthEntry#getDepth()
	 * @see #getForkDepthEntry()
	 * @generated
	 */
	EAttribute getForkDepthEntry_Depth();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.ForkDepthEntry#getCount <em>Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Count</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.ForkDepthEntry#getCount()
	 * @see #getForkDepthEntry()
	 * @generated
	 */
	EAttribute getForkDepthEntry_Count();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack <em>Race Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Race Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack
	 * @generated
	 */
	EClass getRaceAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getPropagationAdvantage <em>Propagation Advantage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Propagation Advantage</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getPropagationAdvantage()
	 * @see #getRaceAttack()
	 * @generated
	 */
	EAttribute getRaceAttack_PropagationAdvantage();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getTransactionADelay <em>Transaction ADelay</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Transaction ADelay</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getTransactionADelay()
	 * @see #getRaceAttack()
	 * @generated
	 */
	EAttribute getRaceAttack_TransactionADelay();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getTransactionBAcceleration <em>Transaction BAcceleration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Transaction BAcceleration</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getTransactionBAcceleration()
	 * @see #getRaceAttack()
	 * @generated
	 */
	EAttribute getRaceAttack_TransactionBAcceleration();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getFeeDifferenceThreshold <em>Fee Difference Threshold</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fee Difference Threshold</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getFeeDifferenceThreshold()
	 * @see #getRaceAttack()
	 * @generated
	 */
	EAttribute getRaceAttack_FeeDifferenceThreshold();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getZeroConfAcceptanceRisk <em>Zero Conf Acceptance Risk</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Zero Conf Acceptance Risk</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getZeroConfAcceptanceRisk()
	 * @see #getRaceAttack()
	 * @generated
	 */
	EAttribute getRaceAttack_ZeroConfAcceptanceRisk();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getObservedDoubleSpends <em>Observed Double Spends</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Observed Double Spends</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getObservedDoubleSpends()
	 * @see #getRaceAttack()
	 * @generated
	 */
	EAttribute getRaceAttack_ObservedDoubleSpends();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getAvgPropagationAdvMs <em>Avg Propagation Adv Ms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Avg Propagation Adv Ms</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getAvgPropagationAdvMs()
	 * @see #getRaceAttack()
	 * @generated
	 */
	EAttribute getRaceAttack_AvgPropagationAdvMs();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack <em>Finney Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Finney Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack
	 * @generated
	 */
	EClass getFinneyAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getNumberOfPreminedBlocks <em>Number Of Premined Blocks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Number Of Premined Blocks</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getNumberOfPreminedBlocks()
	 * @see #getFinneyAttack()
	 * @generated
	 */
	EAttribute getFinneyAttack_NumberOfPreminedBlocks();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getWithholdTime <em>Withhold Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Withhold Time</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getWithholdTime()
	 * @see #getFinneyAttack()
	 * @generated
	 */
	EAttribute getFinneyAttack_WithholdTime();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#isMerchantAcceptsZeroConf <em>Merchant Accepts Zero Conf</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Merchant Accepts Zero Conf</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#isMerchantAcceptsZeroConf()
	 * @see #getFinneyAttack()
	 * @generated
	 */
	EAttribute getFinneyAttack_MerchantAcceptsZeroConf();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getDoubleSpendSuccessCount <em>Double Spend Success Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Double Spend Success Count</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getDoubleSpendSuccessCount()
	 * @see #getFinneyAttack()
	 * @generated
	 */
	EAttribute getFinneyAttack_DoubleSpendSuccessCount();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getObservedTimeToReleaseMs <em>Observed Time To Release Ms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Observed Time To Release Ms</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getObservedTimeToReleaseMs()
	 * @see #getFinneyAttack()
	 * @generated
	 */
	EAttribute getFinneyAttack_ObservedTimeToReleaseMs();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack <em>Selfish Mining Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Selfish Mining Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack
	 * @generated
	 */
	EClass getSelfishMiningAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getPrivateChainLeadTarget <em>Private Chain Lead Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Private Chain Lead Target</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getPrivateChainLeadTarget()
	 * @see #getSelfishMiningAttack()
	 * @generated
	 */
	EAttribute getSelfishMiningAttack_PrivateChainLeadTarget();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getWithholdProbability <em>Withhold Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Withhold Probability</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getWithholdProbability()
	 * @see #getSelfishMiningAttack()
	 * @generated
	 */
	EAttribute getSelfishMiningAttack_WithholdProbability();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#isPublishOnCatchup <em>Publish On Catchup</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Publish On Catchup</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#isPublishOnCatchup()
	 * @see #getSelfishMiningAttack()
	 * @generated
	 */
	EAttribute getSelfishMiningAttack_PublishOnCatchup();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getExpectedForksPerHour <em>Expected Forks Per Hour</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expected Forks Per Hour</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getExpectedForksPerHour()
	 * @see #getSelfishMiningAttack()
	 * @generated
	 */
	EAttribute getSelfishMiningAttack_ExpectedForksPerHour();

	/**
	 * Returns the meta object for the reference list '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getMonitoredNodes <em>Monitored Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Monitored Nodes</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getMonitoredNodes()
	 * @see #getSelfishMiningAttack()
	 * @generated
	 */
	EReference getSelfishMiningAttack_MonitoredNodes();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack <em>Stubborn Mining Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Stubborn Mining Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack
	 * @generated
	 */
	EClass getStubbornMiningAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack#getLeadThreshold <em>Lead Threshold</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lead Threshold</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack#getLeadThreshold()
	 * @see #getStubbornMiningAttack()
	 * @generated
	 */
	EAttribute getStubbornMiningAttack_LeadThreshold();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack#getAbandonThreshold <em>Abandon Threshold</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Abandon Threshold</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack#getAbandonThreshold()
	 * @see #getStubbornMiningAttack()
	 * @generated
	 */
	EAttribute getStubbornMiningAttack_AbandonThreshold();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.EqualForkStubbornAttack <em>Equal Fork Stubborn Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Equal Fork Stubborn Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.EqualForkStubbornAttack
	 * @generated
	 */
	EClass getEqualForkStubbornAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.EqualForkStubbornAttack#getTieResolutionBias <em>Tie Resolution Bias</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Tie Resolution Bias</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.EqualForkStubbornAttack#getTieResolutionBias()
	 * @see #getEqualForkStubbornAttack()
	 * @generated
	 */
	EAttribute getEqualForkStubbornAttack_TieResolutionBias();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.LeadStubbornAttack <em>Lead Stubborn Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Lead Stubborn Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.LeadStubbornAttack
	 * @generated
	 */
	EClass getLeadStubbornAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.LeadStubbornAttack#getMinLeadToPublish <em>Min Lead To Publish</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Lead To Publish</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.LeadStubbornAttack#getMinLeadToPublish()
	 * @see #getLeadStubbornAttack()
	 * @generated
	 */
	EAttribute getLeadStubbornAttack_MinLeadToPublish();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.TrailStubbornAttack <em>Trail Stubborn Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Trail Stubborn Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.TrailStubbornAttack
	 * @generated
	 */
	EClass getTrailStubbornAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.TrailStubbornAttack#isContinueOnTrail <em>Continue On Trail</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Continue On Trail</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.TrailStubbornAttack#isContinueOnTrail()
	 * @see #getTrailStubbornAttack()
	 * @generated
	 */
	EAttribute getTrailStubbornAttack_ContinueOnTrail();

	/**
	 * Returns the meta object for class '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack <em>Majority Attack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Majority Attack</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack
	 * @generated
	 */
	EClass getMajorityAttack();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getHashPowerShare <em>Hash Power Share</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Hash Power Share</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getHashPowerShare()
	 * @see #getMajorityAttack()
	 * @generated
	 */
	EAttribute getMajorityAttack_HashPowerShare();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getExpectedTakeoverTime <em>Expected Takeover Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expected Takeover Time</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getExpectedTakeoverTime()
	 * @see #getMajorityAttack()
	 * @generated
	 */
	EAttribute getMajorityAttack_ExpectedTakeoverTime();

	/**
	 * Returns the meta object for the attribute '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getTargetConfirmationsToBreak <em>Target Confirmations To Break</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Confirmations To Break</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getTargetConfirmationsToBreak()
	 * @see #getMajorityAttack()
	 * @generated
	 */
	EAttribute getMajorityAttack_TargetConfirmationsToBreak();

	/**
	 * Returns the meta object for the reference list '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getColludingPools <em>Colluding Pools</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Colluding Pools</em>'.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getColludingPools()
	 * @see #getMajorityAttack()
	 * @generated
	 */
	EReference getMajorityAttack_ColludingPools();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AttackmodelFactory getAttackmodelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl <em>Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttack()
		 * @generated
		 */
		EClass ATTACK = eINSTANCE.getAttack();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK__TYPE = eINSTANCE.getAttack_Type();

		/**
		 * The meta object literal for the '<em><b>Required Hash Power</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK__REQUIRED_HASH_POWER = eINSTANCE.getAttack_RequiredHashPower();

		/**
		 * The meta object literal for the '<em><b>Depends On Network Delay</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK__DEPENDS_ON_NETWORK_DELAY = eINSTANCE.getAttack_DependsOnNetworkDelay();

		/**
		 * The meta object literal for the '<em><b>Baseline Success Probability</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK__BASELINE_SUCCESS_PROBABILITY = eINSTANCE.getAttack_BaselineSuccessProbability();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTACK__PARAMETERS = eINSTANCE.getAttack_Parameters();

		/**
		 * The meta object literal for the '<em><b>Gamma</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK__GAMMA = eINSTANCE.getAttack_Gamma();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackScenarioImpl <em>Attack Scenario</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackScenarioImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttackScenario()
		 * @generated
		 */
		EClass ATTACK_SCENARIO = eINSTANCE.getAttackScenario();

		/**
		 * The meta object literal for the '<em><b>Attack</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTACK_SCENARIO__ATTACK = eINSTANCE.getAttackScenario_Attack();

		/**
		 * The meta object literal for the '<em><b>Attackers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTACK_SCENARIO__ATTACKERS = eINSTANCE.getAttackScenario_Attackers();

		/**
		 * The meta object literal for the '<em><b>Blockchain System</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM = eINSTANCE.getAttackScenario_BlockchainSystem();

		/**
		 * The meta object literal for the '<em><b>Result</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTACK_SCENARIO__RESULT = eINSTANCE.getAttackScenario_Result();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackerNodeImpl <em>Attacker Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackerNodeImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttackerNode()
		 * @generated
		 */
		EClass ATTACKER_NODE = eINSTANCE.getAttackerNode();

		/**
		 * The meta object literal for the '<em><b>Power Share</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACKER_NODE__POWER_SHARE = eINSTANCE.getAttackerNode_PowerShare();

		/**
		 * The meta object literal for the '<em><b>Linked Node System</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTACKER_NODE__LINKED_NODE_SYSTEM = eINSTANCE.getAttackerNode_LinkedNodeSystem();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl <em>Attack Result</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttackResult()
		 * @generated
		 */
		EClass ATTACK_RESULT = eINSTANCE.getAttackResult();

		/**
		 * The meta object literal for the '<em><b>Success</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__SUCCESS = eINSTANCE.getAttackResult_Success();

		/**
		 * The meta object literal for the '<em><b>Duration</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__DURATION = eINSTANCE.getAttackResult_Duration();

		/**
		 * The meta object literal for the '<em><b>Affected Blocks</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__AFFECTED_BLOCKS = eINSTANCE.getAttackResult_AffectedBlocks();

		/**
		 * The meta object literal for the '<em><b>Success Probability Observed</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED = eINSTANCE.getAttackResult_SuccessProbabilityObserved();

		/**
		 * The meta object literal for the '<em><b>Avg Fork Rate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__AVG_FORK_RATE = eINSTANCE.getAttackResult_AvgForkRate();

		/**
		 * The meta object literal for the '<em><b>Stale Block Rate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__STALE_BLOCK_RATE = eINSTANCE.getAttackResult_StaleBlockRate();

		/**
		 * The meta object literal for the '<em><b>Double Spend Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__DOUBLE_SPEND_COUNT = eINSTANCE.getAttackResult_DoubleSpendCount();

		/**
		 * The meta object literal for the '<em><b>Time To Takeover</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__TIME_TO_TAKEOVER = eINSTANCE.getAttackResult_TimeToTakeover();

		/**
		 * The meta object literal for the '<em><b>Fork Depth Distribution</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION = eINSTANCE.getAttackResult_ForkDepthDistribution();

		/**
		 * The meta object literal for the '<em><b>Attacker Revenue Ratio</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_RESULT__ATTACKER_REVENUE_RATIO = eINSTANCE.getAttackResult_AttackerRevenueRatio();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackParameterImpl <em>Attack Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackParameterImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getAttackParameter()
		 * @generated
		 */
		EClass ATTACK_PARAMETER = eINSTANCE.getAttackParameter();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_PARAMETER__KEY = eINSTANCE.getAttackParameter_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACK_PARAMETER__VALUE = eINSTANCE.getAttackParameter_Value();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.ForkDepthEntryImpl <em>Fork Depth Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.ForkDepthEntryImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getForkDepthEntry()
		 * @generated
		 */
		EClass FORK_DEPTH_ENTRY = eINSTANCE.getForkDepthEntry();

		/**
		 * The meta object literal for the '<em><b>Depth</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FORK_DEPTH_ENTRY__DEPTH = eINSTANCE.getForkDepthEntry_Depth();

		/**
		 * The meta object literal for the '<em><b>Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FORK_DEPTH_ENTRY__COUNT = eINSTANCE.getForkDepthEntry_Count();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl <em>Race Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getRaceAttack()
		 * @generated
		 */
		EClass RACE_ATTACK = eINSTANCE.getRaceAttack();

		/**
		 * The meta object literal for the '<em><b>Propagation Advantage</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RACE_ATTACK__PROPAGATION_ADVANTAGE = eINSTANCE.getRaceAttack_PropagationAdvantage();

		/**
		 * The meta object literal for the '<em><b>Transaction ADelay</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RACE_ATTACK__TRANSACTION_ADELAY = eINSTANCE.getRaceAttack_TransactionADelay();

		/**
		 * The meta object literal for the '<em><b>Transaction BAcceleration</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RACE_ATTACK__TRANSACTION_BACCELERATION = eINSTANCE.getRaceAttack_TransactionBAcceleration();

		/**
		 * The meta object literal for the '<em><b>Fee Difference Threshold</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD = eINSTANCE.getRaceAttack_FeeDifferenceThreshold();

		/**
		 * The meta object literal for the '<em><b>Zero Conf Acceptance Risk</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK = eINSTANCE.getRaceAttack_ZeroConfAcceptanceRisk();

		/**
		 * The meta object literal for the '<em><b>Observed Double Spends</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RACE_ATTACK__OBSERVED_DOUBLE_SPENDS = eINSTANCE.getRaceAttack_ObservedDoubleSpends();

		/**
		 * The meta object literal for the '<em><b>Avg Propagation Adv Ms</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RACE_ATTACK__AVG_PROPAGATION_ADV_MS = eINSTANCE.getRaceAttack_AvgPropagationAdvMs();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl <em>Finney Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getFinneyAttack()
		 * @generated
		 */
		EClass FINNEY_ATTACK = eINSTANCE.getFinneyAttack();

		/**
		 * The meta object literal for the '<em><b>Number Of Premined Blocks</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS = eINSTANCE.getFinneyAttack_NumberOfPreminedBlocks();

		/**
		 * The meta object literal for the '<em><b>Withhold Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FINNEY_ATTACK__WITHHOLD_TIME = eINSTANCE.getFinneyAttack_WithholdTime();

		/**
		 * The meta object literal for the '<em><b>Merchant Accepts Zero Conf</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF = eINSTANCE.getFinneyAttack_MerchantAcceptsZeroConf();

		/**
		 * The meta object literal for the '<em><b>Double Spend Success Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT = eINSTANCE.getFinneyAttack_DoubleSpendSuccessCount();

		/**
		 * The meta object literal for the '<em><b>Observed Time To Release Ms</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS = eINSTANCE.getFinneyAttack_ObservedTimeToReleaseMs();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl <em>Selfish Mining Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getSelfishMiningAttack()
		 * @generated
		 */
		EClass SELFISH_MINING_ATTACK = eINSTANCE.getSelfishMiningAttack();

		/**
		 * The meta object literal for the '<em><b>Private Chain Lead Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET = eINSTANCE
				.getSelfishMiningAttack_PrivateChainLeadTarget();

		/**
		 * The meta object literal for the '<em><b>Withhold Probability</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY = eINSTANCE.getSelfishMiningAttack_WithholdProbability();

		/**
		 * The meta object literal for the '<em><b>Publish On Catchup</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP = eINSTANCE.getSelfishMiningAttack_PublishOnCatchup();

		/**
		 * The meta object literal for the '<em><b>Expected Forks Per Hour</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR = eINSTANCE
				.getSelfishMiningAttack_ExpectedForksPerHour();

		/**
		 * The meta object literal for the '<em><b>Monitored Nodes</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SELFISH_MINING_ATTACK__MONITORED_NODES = eINSTANCE.getSelfishMiningAttack_MonitoredNodes();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.StubbornMiningAttackImpl <em>Stubborn Mining Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.StubbornMiningAttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getStubbornMiningAttack()
		 * @generated
		 */
		EClass STUBBORN_MINING_ATTACK = eINSTANCE.getStubbornMiningAttack();

		/**
		 * The meta object literal for the '<em><b>Lead Threshold</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STUBBORN_MINING_ATTACK__LEAD_THRESHOLD = eINSTANCE.getStubbornMiningAttack_LeadThreshold();

		/**
		 * The meta object literal for the '<em><b>Abandon Threshold</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD = eINSTANCE.getStubbornMiningAttack_AbandonThreshold();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.EqualForkStubbornAttackImpl <em>Equal Fork Stubborn Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.EqualForkStubbornAttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getEqualForkStubbornAttack()
		 * @generated
		 */
		EClass EQUAL_FORK_STUBBORN_ATTACK = eINSTANCE.getEqualForkStubbornAttack();

		/**
		 * The meta object literal for the '<em><b>Tie Resolution Bias</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EQUAL_FORK_STUBBORN_ATTACK__TIE_RESOLUTION_BIAS = eINSTANCE
				.getEqualForkStubbornAttack_TieResolutionBias();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.LeadStubbornAttackImpl <em>Lead Stubborn Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.LeadStubbornAttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getLeadStubbornAttack()
		 * @generated
		 */
		EClass LEAD_STUBBORN_ATTACK = eINSTANCE.getLeadStubbornAttack();

		/**
		 * The meta object literal for the '<em><b>Min Lead To Publish</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LEAD_STUBBORN_ATTACK__MIN_LEAD_TO_PUBLISH = eINSTANCE.getLeadStubbornAttack_MinLeadToPublish();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.TrailStubbornAttackImpl <em>Trail Stubborn Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.TrailStubbornAttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getTrailStubbornAttack()
		 * @generated
		 */
		EClass TRAIL_STUBBORN_ATTACK = eINSTANCE.getTrailStubbornAttack();

		/**
		 * The meta object literal for the '<em><b>Continue On Trail</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRAIL_STUBBORN_ATTACK__CONTINUE_ON_TRAIL = eINSTANCE.getTrailStubbornAttack_ContinueOnTrail();

		/**
		 * The meta object literal for the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.MajorityAttackImpl <em>Majority Attack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.MajorityAttackImpl
		 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelPackageImpl#getMajorityAttack()
		 * @generated
		 */
		EClass MAJORITY_ATTACK = eINSTANCE.getMajorityAttack();

		/**
		 * The meta object literal for the '<em><b>Hash Power Share</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAJORITY_ATTACK__HASH_POWER_SHARE = eINSTANCE.getMajorityAttack_HashPowerShare();

		/**
		 * The meta object literal for the '<em><b>Expected Takeover Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAJORITY_ATTACK__EXPECTED_TAKEOVER_TIME = eINSTANCE.getMajorityAttack_ExpectedTakeoverTime();

		/**
		 * The meta object literal for the '<em><b>Target Confirmations To Break</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAJORITY_ATTACK__TARGET_CONFIRMATIONS_TO_BREAK = eINSTANCE
				.getMajorityAttack_TargetConfirmationsToBreak();

		/**
		 * The meta object literal for the '<em><b>Colluding Pools</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAJORITY_ATTACK__COLLUDING_POOLS = eINSTANCE.getMajorityAttack_ColludingPools();

	}

} //AttackmodelPackage
