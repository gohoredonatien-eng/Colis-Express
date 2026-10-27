const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_ci_123";
const BOB_UID = "bob_ci_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// 1. Unauthenticated checks
test("Unauthenticated user: cannot read user parcels", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("parcels").get());
});

// 2. Cross user access checks
test("Authenticated user: cannot read another user's parcel", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("parcels").doc("p1").set({
      reference: "IVO-CI-1111",
      senderName: "Bob",
      senderPhone: "0701020304",
      senderCommune: "Cocody",
      senderAddress: "Faya",
      recipientName: "Awa",
      recipientPhone: "0505060708",
      destinationCity: "Bouaké",
      destinationAddress: "Commerce",
      parcelCategory: "Textile",
      weightKg: 2.0,
      description: "Colis pagnes",
      priceFcfa: 2000,
      status: "DEMANDE_RECU",
      paymentStatus: "NON_PAYE",
      userId: BOB_UID,
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("parcels").doc("p1").get());
});

// 3. User can create their own valid parcel
test("Authenticated user: can create their own valid parcel", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("parcels").doc("p_alice_1").set({
      reference: "IVO-CI-9999",
      senderName: "Alice",
      senderPhone: "0767182545",
      senderCommune: "Cocody",
      senderAddress: "Faya Nouveau Goudron",
      recipientName: "Kouassi",
      recipientPhone: "0102030405",
      destinationCity: "Yamoussoukro",
      destinationAddress: "Gare UTB",
      parcelCategory: "Documents",
      weightKg: 1.0,
      description: "Dossier express",
      priceFcfa: 1500,
      status: "DEMANDE_RECU",
      paymentStatus: "NON_PAYE",
      userId: ALICE_UID,
      createdAt: new Date(),
      updatedAt: new Date(),
      history: []
    })
  );
});

// 4. Invalid parcel with price < 1500 FCFA fails
test("Authenticated user: cannot create parcel with price below 1500 FCFA", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("parcels").doc("p_invalid").set({
      reference: "IVO-CI-0001",
      senderName: "Alice",
      senderPhone: "0767182545",
      senderCommune: "Cocody",
      senderAddress: "Faya",
      recipientName: "Kouassi",
      recipientPhone: "0102030405",
      destinationCity: "Bouaké",
      destinationAddress: "Gare",
      parcelCategory: "Pli",
      weightKg: 0.5,
      description: "Pli",
      priceFcfa: 500, // Invalid! minimum is 1500 FCFA
      status: "DEMANDE_RECU",
      paymentStatus: "NON_PAYE",
      userId: ALICE_UID,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});
