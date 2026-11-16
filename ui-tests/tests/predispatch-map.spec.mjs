import { expect, test } from '@playwright/test';

test('abre el mapa de pedidos y conserva el formulario de rutas', async ({ page }, testInfo) => {
  const user = process.env.IDEMPIERE_TEST_USER;
  const password = process.env.IDEMPIERE_TEST_PASSWORD;
  test.skip(!user || !password, 'Requiere una cuenta autorizada de la instancia de prueba');

  await page.goto('/webui/index.zul');
  await page.locator('input[type="password"]').first().waitFor({ state: 'visible' });
  await page.locator('input[type="text"]').first().fill(user);
  await page.locator('input[type="password"]').first().fill(password);
  await page.getByRole('button', { name: 'OK' }).click();
  await page.locator('.z-combobox-input').first().waitFor({ state: 'visible' });
  const role = process.env.IDEMPIERE_TEST_ROLE;
  if (role) {
    await page.getByText('Select Role', { exact: true }).waitFor({ state: 'visible' });
    await page.locator('.z-combobox-input').nth(1).locator('..').locator('.z-combobox-button').click();
    await page.getByText(role, { exact: true }).click();
  }
  await page.getByRole('button', { name: 'OK' }).last().click();

  const search = page.getByPlaceholder('Alt+G');
  await search.waitFor({ state: 'visible' });
  if (role) {
    await page.getByText(role, { exact: false }).first().waitFor({ state: 'visible' });
  } else {
    await page.getByText(/^Home \(/).first().waitFor({ state: 'visible' });
  }
  await search.fill('Mapa de Predespacho');
  const mapResult = page.locator('.z-bandbox-popup').getByText('Mapa de Predespacho', { exact: true });
  await expect(mapResult).toBeVisible();
  await page.waitForTimeout(700);
  const basemapResponse = page.waitForResponse(
    response => response.url().startsWith('https://tiles.openfreemap.org/styles/liberty') && response.status() === 200,
    { timeout: 30_000 },
  );
  await mapResult.click();

  await expect(page.getByRole('button', { name: 'Actualizar' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Seleccionar por área' })).toBeVisible();
  const reviewProposal = page.getByRole('button', { name: 'Revisar propuesta temporal' });
  await expect(reviewProposal).toBeVisible();
  await expect(reviewProposal).toBeDisabled();
  await expect(page.getByText(/\d+ de \d+ pedidos/)).toBeVisible();
  await expect(page.locator('iframe:visible').last()).toBeVisible();
  await basemapResponse;
  await expect(page.frameLocator('iframe:visible').getByText('OpenFreeMap')).toBeVisible();
  await page.waitForTimeout(3000);
  await page.screenshot({ path: testInfo.outputPath('predispatch-map.png'), fullPage: true });

  if (process.env.IDEMPIERE_TEST_FIXTURE === '1') {
    await expect(page.getByText('12 de 12 pedidos')).toBeVisible();
    if (process.env.IDEMPIERE_TEST_HISTORICAL !== '1') {
      await expect(page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-01' })).toContainText('Menos de 24 h');
      await page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-02' }).click();
      await expect(reviewProposal).toBeEnabled();
      await reviewProposal.click();
      await expect(page.getByText('Propuesta temporal · no guardada', { exact: true })).toBeVisible();
      await expect(page.getByText(/BORRADOR TEMPORAL · NO GUARDADO/)).toBeVisible();
      await expect(page.getByText(/IQP-MAP-02/).last()).toBeVisible();
      await page.getByRole('button', { name: 'OK' }).click();
      await page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-02' }).click();
      await expect(reviewProposal).toBeDisabled();
    }
    await expect(page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-02' })).toContainText('De 24 a 47 h');
    await expect(page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-03' })).toContainText('48 h o más');
    await expect(page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-04' })).toContainText('48 h o más');
    await expect(page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-09' })).toContainText('48 h o más');
    if (process.env.IDEMPIERE_TEST_PARTIAL === '1') {
      const partial = page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-01' });
      await expect(partial.locator('.z-listcell').nth(7)).toHaveText('1');
      await expect(partial).not.toContainText('Despachado');
    }
    if (process.env.IDEMPIERE_TEST_HISTORICAL === '1') {
      const historical = page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-01' });
      await expect(page.getByText(/1 despachados/)).toBeVisible();
      await expect(historical).toContainText('Despachado');
      await expect(historical.locator('.z-listcell').nth(7)).toHaveText('0');
      await expect(historical).toHaveClass(/z-listitem-disabled/);
      await historical.click({ force: true });
      await expect(page.getByText(/0 seleccionados/)).toBeVisible();
      await page.getByPlaceholder('Pedido o cliente').fill('IQP-MAP-01');
      await page.getByPlaceholder('Pedido o cliente').press('Enter');
      await expect(page.getByText('1 de 12 pedidos')).toBeVisible();
      await expect(page.frameLocator('iframe:visible').getByText('OpenFreeMap')).toBeVisible();
      await page.waitForTimeout(1500);
      await page.screenshot({ path: testInfo.outputPath('historical-filtered.png'), fullPage: true });
      await page.getByPlaceholder('Pedido o cliente').fill('');
      await page.getByPlaceholder('Pedido o cliente').press('Enter');
    }

    const filters = page.locator('select');
    await expect(filters.nth(0).locator('option')).toHaveCount(6);
    await expect(filters.nth(1).locator('option')).toHaveCount(10);
    await expect(filters.nth(2).locator('option')).toHaveCount(13);
    await filters.nth(0).selectOption({ label: 'Portuguesa' });
    await expect(page.getByText('2 de 12 pedidos')).toBeVisible();
    await filters.nth(1).selectOption({ label: 'Turén' });
    await filters.nth(2).selectOption({ label: 'VILLA BRUZUAL' });
    await expect(page.getByText('1 de 12 pedidos')).toBeVisible();
    await expect(page.locator('.z-listitem').filter({ hasText: 'IQP-MAP-06' })).toContainText('VILLA BRUZUAL');
    await expect(page.frameLocator('iframe:visible').getByText('OpenFreeMap')).toBeVisible();
    await page.waitForTimeout(2000);
    await page.screenshot({ path: testInfo.outputPath('predispatch-filtered.png'), fullPage: true });
    await filters.nth(0).selectOption({ index: 0 });
    await filters.nth(1).selectOption({ index: 0 });
    await filters.nth(2).selectOption({ index: 0 });
  }

  await search.fill('Route View Form');
  const routeResult = page.locator('.z-bandbox-popup').getByText('Route View Form based FO', { exact: true });
  await expect(routeResult).toBeVisible();
  await page.waitForTimeout(700);
  await routeResult.click();
  await page.keyboard.press('Escape');
  await expect(page.locator('.z-tab-text').getByText('Route View Form based FO', { exact: true })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Refresh FO' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Show Route [GraphHopper]' })).toBeDisabled();
  await page.screenshot({ path: testInfo.outputPath('freight-route-form.png'), fullPage: true });
});
